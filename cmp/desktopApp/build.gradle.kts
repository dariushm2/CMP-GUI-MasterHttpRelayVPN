
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

sourceSets {
    main {
        resources {
            val os = System.getProperty("os.name").lowercase()
            if (!os.contains("mac")) exclude("macos/**")
            if (!os.contains("win")) exclude("windows/**")
            if (!os.contains("linux")) exclude("linux/**")
        }
    }
}

val prepareAppResourcesForPackaging = tasks.register<Copy>("prepareAppResourcesForPackaging") {
    dependsOn("bundlePythonExecutable")
    val os = System.getProperty("os.name").lowercase()
    from(project.file("src/main/resources"))
    into(layout.buildDirectory.dir("tmp/packaged-resources"))
    
    if (!os.contains("mac")) exclude("macos/**")
    if (!os.contains("win")) exclude("windows/**")
    if (!os.contains("linux")) exclude("linux/**")
}

compose.desktop {
    application {
        mainClass = "com.darius.lionvpn.MainKt"
        jvmArgs("-Dsun.awt.wmclass=lion-vpn")

        buildTypes.release.proguard {
            isEnabled.set(true)
            obfuscate.set(false) // Shrinks without obfuscation (safer default for reflection, avoids crashes)
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