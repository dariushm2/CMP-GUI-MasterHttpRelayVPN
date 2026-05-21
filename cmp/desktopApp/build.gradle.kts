import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.nio.file.Paths

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.detekt)
}

dependencies {
    implementation(projects.shared)
    implementation(compose.foundation)
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.ktor.okhttp)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.test)
}

compose.desktop {
    application {
        mainClass = "com.darius.relay_vpn.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "lion-vpn"
            packageVersion = rootProject.extra["versionName"] as String
            appResourcesRootDir.set(project.layout.projectDirectory.dir("src/main/resources"))
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
}