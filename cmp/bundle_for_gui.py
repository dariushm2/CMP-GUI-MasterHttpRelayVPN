#!/usr/bin/env python3
"""Cross-platform script to bundle the Python VPN backend into the Kotlin Multiplatform GUI app.

Uses PyInstaller to compile the Python server into a standalone executable
and moves it to the appropriate platform-specific resources directory.
Includes incremental build checking to skip rebuilds when sources haven't changed.
"""

import argparse
import os
import shutil
import subprocess
import sys
from pathlib import Path


def log(msg: str) -> None:
    print(f"[Bundle Script] {msg}")


def error(msg: str) -> None:
    print(f"[Bundle Script] ERROR: {msg}", file=sys.stderr)


def get_latest_modification_time(paths_to_check: list[Path]) -> float:
    latest = 0.0
    for path in paths_to_check:
        if path.is_file():
            latest = max(latest, path.stat().st_mtime)
        elif path.is_dir():
            for root, _, files in os.walk(path):
                # Skip virtual environments and packaging dirs if they are nested
                if any(x in root for x in [".venv", "venv", "build", "dist", ".git", "cmp"]):
                    continue
                for f in files:
                    file_path = Path(root) / f
                    try:
                        latest = max(latest, file_path.stat().st_mtime)
                    except FileNotFoundError:
                        pass
    return latest


def ensure_pyinstaller_installed() -> bool:
    try:
        import PyInstaller
        return True
    except ImportError:
        log("PyInstaller not found. Attempting to install dependencies...")
        try:
            subprocess.run(
                [sys.executable, "-m", "pip", "install", "--upgrade", "pip"],
                check=True
            )
            subprocess.run(
                [sys.executable, "-m", "pip", "install", "-r", "requirements.txt", "pyinstaller"],
                check=True
            )
            import PyInstaller
            log("Successfully installed PyInstaller and requirements.")
            return True
        except Exception as e:
            error(f"Failed to install PyInstaller or requirements: {e}")
            return False


def check_and_bootstrap_venv() -> None:
    in_venv = (sys.prefix != sys.base_prefix) or hasattr(sys, 'real_prefix')
    if in_venv:
        return

    script_dir = Path(__file__).resolve().parent
    repo_root = script_dir.parent
    venv_dir = repo_root / ".venv"

    if sys.platform.startswith("win"):
        venv_python = venv_dir / "Scripts" / "python.exe"
    else:
        venv_python = venv_dir / "bin" / "python"

    if not venv_dir.exists():
        log(f"Virtual environment not found at {venv_dir}. Creating it...")
        try:
            subprocess.run([sys.executable, "-m", "venv", str(venv_dir)], check=True)
            log("Virtual environment created successfully.")
        except Exception as e:
            error(f"Failed to create virtual environment: {e}")
            sys.exit(1)

    if venv_python.exists():
        log(f"Re-executing script within virtual environment: {venv_python}")
        os.execv(str(venv_python), [str(venv_python)] + sys.argv)
    else:
        error(f"Virtual environment python not found at {venv_python}")
        sys.exit(1)


def main() -> int:
    check_and_bootstrap_venv()

    parser = argparse.ArgumentParser(description="Bundle Python backend for KMP GUI.")
    parser.add_argument("--force", action="store_true", help="Force rebuild even if sources haven't changed.")
    args = parser.parse_args()

    # Determine absolute paths relative to cmp root
    script_dir = Path(__file__).resolve().parent
    repo_root = script_dir.parent

    # Switch working directory to repo root so relative paths work
    os.chdir(repo_root)

    # 1. Determine target platform and resources path
    os_name = sys.platform
    if os_name.startswith("win"):
        platform_name = "windows"
        bin_name = "MasterHttpRelayVPN.exe"
    elif os_name.startswith("darwin"):
        platform_name = "macos"
        bin_name = "MasterHttpRelayVPN"
    else:
        platform_name = "linux"
        bin_name = "MasterHttpRelayVPN"

    dest_dir = repo_root / "cmp" / "desktopApp" / "src" / "main" / "resources" / platform_name
    dest_path = dest_dir / bin_name

    log(f"Platform detected: {platform_name}")
    log(f"Target path: {dest_path}")

    # Copy config.example.json to common resources so it is always packaged
    dest_dir.mkdir(parents=True, exist_ok=True)
    example_src = repo_root / "config.example.json"
    example_dest = dest_dir / "config.example.json"
    if example_src.exists():
        log(f"Copying config.example.json to {example_dest}")
        shutil.copy2(example_src, example_dest)

    # 2. Check if we need to compile (Incremental Build)
    if not args.force and dest_path.exists():
        # Check source file modification times
        sources = [
            repo_root / "main.py",
            repo_root / "requirements.txt",
            repo_root / "src",
        ]
        latest_src_time = get_latest_modification_time(sources)
        dest_time = dest_path.stat().st_mtime

        if latest_src_time < dest_time:
            log("Python sources haven't changed. Skipping bundling process (use --force to override).")
            return 0
        else:
            log("Python sources have changed. Rebuilding...")
    else:
        log("No existing binary found or force flag enabled. Building...")

    # 3. Ensure PyInstaller and requirements are present
    if not ensure_pyinstaller_installed():
        return 1

    # 4. Run PyInstaller
    log("Running PyInstaller...")
    cmd = [
        sys.executable,
        "-m",
        "PyInstaller",
        "--noconfirm",
        "--clean",
        "--onefile",
        "--name", "MasterHttpRelayVPN",
        "--paths", "src",
        "main.py"
    ]

    try:
        subprocess.run(cmd, check=True, capture_output=True, text=True)
    except subprocess.CalledProcessError as e:
        error(f"PyInstaller execution failed: {e}")
        print("--- PYINSTALLER STDOUT ---", file=sys.stderr)
        print(e.stdout, file=sys.stderr)
        print("--- PYINSTALLER STDERR ---", file=sys.stderr)
        print(e.stderr, file=sys.stderr)
        return 1

    # 5. Move executable to resources
    built_path = repo_root / "dist" / bin_name
    if not built_path.exists():
        error(f"Expected compiled binary at '{built_path}' but it does not exist.")
        return 1

    log(f"Moving compiled binary to resources directory: {dest_path}")
    dest_dir.mkdir(parents=True, exist_ok=True)
    
    # Try to copy and then remove original to avoid cross-device issues
    try:
        shutil.copy2(built_path, dest_path)
        # Ensure executable permissions on non-windows
        if not os_name.startswith("win"):
            os.chmod(dest_path, 0o755)
    except Exception as e:
        error(f"Failed to copy binary to resource directory: {e}")
        return 1

    # 6. Cleanup temporary PyInstaller files
    log("Cleaning up temporary PyInstaller directories...")
    for temp_dir in ["build", "dist"]:
        path = repo_root / temp_dir
        if path.exists():
            try:
                shutil.rmtree(path)
            except Exception as e:
                log(f"Warning: Could not remove temporary directory {path}: {e}")
                
    spec_file = repo_root / "MasterHttpRelayVPN.spec"
    if spec_file.exists():
        try:
            spec_file.unlink()
        except Exception as e:
            log(f"Warning: Could not remove spec file {spec_file}: {e}")

    log("Successfully bundled Python backend for the GUI app!")
    return 0


if __name__ == "__main__":
    sys.exit(main())
