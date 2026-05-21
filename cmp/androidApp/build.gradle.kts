import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.detekt)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

apply(from = file("${rootProject.rootDir}/dependencies/detekt/detekt.gradle"))

android {
    namespace = "com.darius.lionvpn"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.darius.lionvpn"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String
    }

    signingConfigs {
        named("debug") {
            storeFile = file("debug.jks")
        }
        create("release") {
            storeFile = file("release.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "defaultPassword"
            keyAlias = "releaseKeyAlias"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "defaultKeyPassword"
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
            versionNameSuffix = "-debug"
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

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = getApkName(variant.versionName, variant.baseName)
                output.outputFileName = outputFileName
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

dependencies {
    implementation(projects.shared)
    implementation(compose.foundation)

    implementation(libs.timber)

    implementation(compose.preview)
    implementation(libs.androidx.activity.compose)

    implementation(libs.ktor.okhttp) // OkHttp for Android
    implementation(libs.koin.android)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.test)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)
}

fun getApkName(versionName: String, buildType: String): String {
    val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
    return "lion-vpn-$versionName-$buildType-$date.apk"
}
