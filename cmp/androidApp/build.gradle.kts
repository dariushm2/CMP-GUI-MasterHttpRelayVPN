import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat
import java.util.Date
import java.net.URI

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    id("com.chaquo.python") version "17.0.0"
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(file("${rootDir}/dependencies/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
}

extensions.configure<ApplicationExtension> {
    namespace = "com.darius.lionvpn"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.darius.lionvpn"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    signingConfigs {
        named("debug") {
            storeFile = file("debug.keystore")
        }
        create("release") {
            storeFile = file("release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "android"
            keyAlias = "releaseKeyAlias"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "android"
        }
    }

    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val versionName = rootProject.extra["versionName"] as String
            val outputFileName = getApkName(versionName, variant.name)
            output.outputFileName = outputFileName
        }
    }
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    implementation(projects.shared)
    implementation(libs.foundation)
    implementation(libs.material3)
    implementation(libs.ui)
    implementation(libs.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.ui.tooling.preview)

    implementation(libs.timber)

    implementation(libs.ktor.okhttp) // OkHttp for Android
    implementation(libs.koin.android)
    implementation(libs.kotlinx.serialization)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
}

fun getApkName(versionName: String, buildType: String): String {
    val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
    return "lion-vpn-$versionName-arm64-v8a-$buildType-$date.apk"
}

fun Project.getOrDownloadPython(): String {
    val osName = System.getProperty("os.name").lowercase()
    val osArch = System.getProperty("os.arch").lowercase()
    
    val (osKey, archKey, exePath) = when {
        osName.contains("win") -> Triple("pc-windows-msvc-shared", "x86_64", "python/python.exe")
        osName.contains("mac") || osName.contains("darwin") -> {
            val arch = if (osArch.contains("aarch64") || osArch.contains("arm64")) "aarch64" else "x86_64"
            Triple("apple-darwin", arch, "python/bin/python3")
        }
        else -> { // Linux/other
            val arch = if (osArch.contains("aarch64") || osArch.contains("arm64")) "aarch64" else "x86_64"
            Triple("unknown-linux-gnu", arch, "python/bin/python3")
        }
    }
    
    val pythonDir = file("${projectDir}/.gradle/python-3.10")
    val exeFile = File(pythonDir, exePath)
    
    if (!exeFile.exists()) {
        val version = "3.10.15"
        val date = "20241016"
        val fileName = "cpython-$version+$date-$archKey-$osKey-install_only.tar.gz"
        val downloadUrl = "https://github.com/astral-sh/python-build-standalone/releases/download/$date/$fileName"
        
        println("Downloading portable Python 3.10 for Chaquopy from $downloadUrl...")
        pythonDir.mkdirs()
        val archiveFile = File(pythonDir, fileName)
        
        try {
            URI(downloadUrl).toURL().openStream().use { input ->
                archiveFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            println("Extracting $fileName...")
            if (osName.contains("win")) {
                copy {
                    from(tarTree(resources.gzip(archiveFile)))
                    into(pythonDir)
                }
            } else {
                val process = ProcessBuilder("tar", "-xzf", archiveFile.absolutePath, "-C", pythonDir.absolutePath)
                    .redirectErrorStream(true)
                    .start()
                val exitCode = process.waitFor()
                if (exitCode != 0) {
                    val output = process.inputStream.bufferedReader().readText()
                    throw GradleException("Failed to extract Python archive (exit code $exitCode): $output")
                }
            }
            
            // Clean up archive
            archiveFile.delete()
            
            // On Linux/macOS, ensure the executable is actually executable
            if (!osName.contains("win")) {
                exeFile.setExecutable(true)
                File(pythonDir, "python/bin/python").setExecutable(true)
            }
            
            println("Portable Python 3.10 configured successfully at ${exeFile.absolutePath}")
        } catch (e: Exception) {
            archiveFile.delete()
            throw GradleException("Failed to download or extract portable Python 3.10 for Chaquopy. Please check your internet connection.", e)
        }
    }
    
    return exeFile.absolutePath
}

chaquopy {
    defaultConfig {
        version = "3.10"
        buildPython(project.getOrDownloadPython())
        pip {
            install("cryptography>=41.0.0")
            install("h2>=4.1.0")
            install("certifi>=2024.1.0")
            install("brotli>=1.0.7")
            install("zstandard>=0.15.2")
        }
    }
}

val copyPythonSources = tasks.register<Copy>("copyPythonSources") {
    description = "Copy python src dir into Android python/src dir"
    from(file("../../src"))
    into(file("src/main/python/src"))
}

tasks.named("preBuild") {
    dependsOn(copyPythonSources)
}

tasks.configureEach {
    if (name.startsWith("merge") && name.endsWith("PythonSources")) {
        dependsOn(copyPythonSources)
    }
}
