import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
}

val generateBuildConfig = tasks.register("generateBuildConfig") {
    val buildConfigDir = layout.buildDirectory.dir("generated/source/buildConfig/commonMain/kotlin")
    outputs.dir(buildConfigDir)
    doLast {
        val versionName = rootProject.extra["versionName"] as? String ?: "1.0.0"
        val outputFile = buildConfigDir.get().file("com/darius/lionvpn/BuildConfig.kt").asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText("""
            package com.darius.lionvpn

            public object BuildConfig {
                public const val APP_VERSION: String = "$versionName"
            }
        """.trimIndent())
    }
}

kotlin {
    android {
        namespace = "com.darius.lionvpn.shared"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }


    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        commonMain {
            kotlin.srcDir(generateBuildConfig)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.okhttp) // OkHttp for Android
            implementation(libs.koin.android)

            implementation(libs.timber)
        }
        iosMain.dependencies {
            implementation(libs.ktor.darwin) // Native Darwin client for iOS
            implementation(libs.kotlinx.coroutinesCore) // Native coroutines
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.jetbrainsNavigationCompose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.ktor.client.core) // Core Ktor client
            implementation(libs.ktor.client.content)
            implementation(libs.ktor.client.serialization) // JSON serialization
            implementation(libs.ktor.serialization.kotlinx)
            implementation(libs.kotlinx.serialization)
            implementation(libs.koin.core) // Adjust version as needed
            implementation(libs.koin.compose) // Adjust version as needed
            implementation(libs.koin.compose.viewmodel) // Adjust version as needed
            implementation(libs.koin.test) // Optional: For testing

        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

compose.resources {
    publicResClass = true
}

dependencies {

}
