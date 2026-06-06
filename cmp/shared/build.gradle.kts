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

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(file("${rootDir}/dependencies/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
}

val generateBuildConfig = tasks.register("generateBuildConfig") {
    description = "Generate SharedBuildConfig"
    val buildConfigDir = layout.buildDirectory.dir("generated/source/buildConfig/commonMain/kotlin")
    outputs.dir(buildConfigDir)
    doLast {
        val versionName = rootProject.extra["versionName"] as? String ?: "1.0.0"
        val outputFile = buildConfigDir.get().file("com/darius/lionvpn/BuildConfig.kt").asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText("""
            package com.darius.lionvpn

            public object SharedBuildConfig {
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
        androidResources {
            enable = true
        }
    }

    listOf(
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
            implementation(libs.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.okhttp)
            implementation(libs.koin.android)

            implementation(libs.timber)
        }
        iosMain.dependencies {
            implementation(libs.ktor.darwin)
            implementation(libs.kotlinx.coroutinesCore)
        }
        commonMain.dependencies {
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.material.icons.extended)
            implementation(libs.ui)
            implementation(libs.components.resources)
            implementation(libs.ui.tooling.preview)
            implementation(libs.jetbrainsNavigationCompose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.serialization.kotlinx)
            implementation(libs.kotlinx.serialization)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
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
    detektPlugins(libs.detekt.formatting)
}
