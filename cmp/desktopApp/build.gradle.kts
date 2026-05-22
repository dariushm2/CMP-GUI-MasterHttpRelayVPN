import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.nio.file.Paths

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.detekt)
}

dependencies {
    implementation(projects.shared)
    implementation(compose.foundation)
    implementation(compose.desktop.currentOs)
    implementation(compose.components.resources)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.kotlinx.serialization)

    implementation(libs.ktor.okhttp)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.test)
}

compose.desktop {
    application {
        mainClass = "com.darius.lionvpn.MainKt"
        jvmArgs("-Dsun.awt.wmclass=lion-vpn")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "lion-vpn"
            packageVersion = rootProject.extra["versionName"] as String
            appResourcesRootDir.set(project.layout.projectDirectory.dir("src/main/resources"))

            macOS {
                iconFile.set(project.file("src/main/resources/macos/icon.icns"))
            }
            windows {
                iconFile.set(project.file("src/main/resources/windows/icon.ico"))
            }
            linux {
                iconFile.set(project.file("src/main/resources/linux/icon.png"))
                shortcut = true
                appCategory = "Network"
                menuGroup = "Network"
                debMaintainer = "support@lionvpn.com"
            }
        }
    }
}

tasks.register<Exec>("bundlePythonExecutable") {
    group = "build"
    description = "Compiles the Python backend using PyInstaller and places it in resources."
    
    val repoRoot = rootProject.projectDir.parentFile
    workingDir = repoRoot

    val isWindows = System.getProperty("os.name").lowercase().contains("win")
    
    // Dynamically detect and use the active virtual environment (.venv) to bypass system PEP 668 package blocks
    val venvPython = if (isWindows) {
        File(repoRoot, ".venv/Scripts/python.exe")
    } else {
        File(repoRoot, ".venv/bin/python")
    }
    
    val pythonCmd = if (venvPython.exists()) {
        venvPython.absolutePath
    } else if (isWindows) {
        "python"
    } else {
        "python3"
    }

    commandLine(pythonCmd, "cmp/bundle_for_gui.py")
}

// Hook the bundle task into standard execution and packaging tasks
tasks.configureEach {
    if (name == "run" || name.startsWith("package") || name.startsWith("createDistributable")) {
        dependsOn("bundlePythonExecutable")
    }

    if (name == "createDistributable" || name == "createReleaseDistributable") {
        doLast {
            val composeBinariesDir = project.layout.buildDirectory.dir("compose/binaries").get().asFile
            if (composeBinariesDir.exists()) {
                composeBinariesDir.walkTopDown().forEach { dir ->
                    if (dir.isDirectory) {
                        val binDir = File(dir, "bin")
                        val libDir = File(dir, "lib")
                        if (binDir.exists() && binDir.isDirectory && libDir.exists() && libDir.isDirectory) {
                            val executables = binDir.listFiles()?.filter { it.isFile && !it.name.startsWith(".") } ?: emptyList()
                            if (executables.isNotEmpty()) {
                                executables.forEach { executable ->
                                    val realBinary = File(libDir, "${executable.name}-real")
                                    if (executable.exists()) {
                                        executable.renameTo(realBinary)
                                        val wrapperScript = File(dir, executable.name)
                                        wrapperScript.writeText("""
                                            #!/bin/sh
                                            # Resolve the directory of this script (the root directory)
                                            DIR="$(cd "$(dirname "${'$'}0")" && pwd)"
                                            # Execute the real native binary launcher in the lib folder
                                            exec "${'$'}DIR/lib/${executable.name}-real" "${'$'}@"
                                        """.trimIndent())
                                        wrapperScript.setExecutable(true, false)
                                        println("[Post-Build] Relocated Linux executable to ${wrapperScript.absolutePath} next to lib/ and removed bin/")
                                    }
                                }
                                binDir.deleteRecursively()
                            }
                        }
                    }
                }
            }
        }
    }
}