
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


val prepareAppResourcesForPackaging = tasks.register<Copy>("prepareAppResourcesForPackaging") {
    dependsOn("bundlePythonExecutable")
    val os = System.getProperty("os.name").lowercase()
    val platformDirName = when {
        os.contains("mac") -> "macos"
        os.contains("win") -> "windows"
        else -> "linux"
    }
    
    // Copy shared resources
    from(project.file("src/main/resources"))
    // Copy platform-specific resources directly into the root of packaged resources
    from(project.file("src/$platformDirName/resources"))
    
    into(layout.buildDirectory.dir("tmp/packaged-resources/common"))
}

compose.desktop {
    application {
        mainClass = "com.darius.lionvpn.MainKt"
        jvmArgs("-Dsun.awt.wmclass=lion-vpn")

        buildTypes.release.proguard {
            isEnabled.set(true)
            obfuscate.set(true) // Enables aggressive class and member renaming for maximum size reduction
            configurationFiles.from(project.file("compose-desktop.pro"))
        }

        nativeDistributions {
            packageName = "lion-vpn"
            val rawVersion = rootProject.extra["versionName"] as String
            packageVersion = rawVersion.split("-")[0]
            appResourcesRootDir.set(layout.buildDirectory.dir("tmp/packaged-resources"))

            // Optimize the bundled JDK runtime image to only what is needed by the app
            modules("java.instrument", "java.management", "jdk.unsupported")

            macOS {
                iconFile.set(project.file("src/macos/resources/icon.icns"))
            }
            windows {
                iconFile.set(project.file("src/windows/resources/icon.ico"))
            }
            linux {
                iconFile.set(project.file("src/linux/resources/icon.png"))
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
    val isPackagingOrRunning = name.contains("package", ignoreCase = true) || 
                               name.contains("Distributable", ignoreCase = true) || 
                               name == "run"
                               
    if (isPackagingOrRunning) {
        dependsOn("bundlePythonExecutable")
    }
    
    val isPackagingOnly = name.contains("package", ignoreCase = true) || 
                          name.contains("Distributable", ignoreCase = true)
                          
    if (isPackagingOnly) {
        dependsOn("prepareAppResourcesForPackaging")
    }
    
    if (name == "prepareAppResources") {
        dependsOn("prepareAppResourcesForPackaging")
    }
}

tasks.withType<org.jetbrains.compose.desktop.application.tasks.AbstractJLinkTask>().configureEach {
    freeArgs.addAll("--compress=2", "--strip-debug", "--no-header-files", "--no-man-pages")
}