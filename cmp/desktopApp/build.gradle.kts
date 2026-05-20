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
            packageName = "com.darius.relay_vpn"
            packageVersion = rootProject.extra["versionName"] as String
            appResourcesRootDir.set(project.layout.projectDirectory.dir("src/main/resources"))
            // Change "python-3.x.x-embed-amd64" to match your exact folder name on disk
            Paths.get("src/main/resources", "python-embed", "python.exe").toString()

        }
    }
}