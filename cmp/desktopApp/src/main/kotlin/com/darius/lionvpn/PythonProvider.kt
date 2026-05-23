package com.darius.lionvpn

import kotlinx.io.files.FileNotFoundException
import java.io.File

fun findRepoRoot(): File {
    val dir = File(System.getProperty("user.dir")).parentFile
    return if (File(dir, "config.example.json").exists()) dir
    else throw FileNotFoundException()
}

fun findResourcesDir(): File {
    val resourcesProp = System.getProperty("compose.application.resources.dir")
    if (resourcesProp != null) {
        return File(resourcesProp)
    }
    val repoRoot = findRepoRoot()
    val devResources = File(repoRoot, "cmp/desktopApp/src/main/resources")
    return if (devResources.exists()) devResources
    else File(System.getProperty("user.dir"), "src/main/resources")
}

fun getPythonExecutablePath(): String {
    val resourcesDir = findResourcesDir()
    val os = System.getProperty("os.name").lowercase()
    
    return when {
        os.contains("win") -> {
            val fileInRoot = File(resourcesDir, "MasterHttpRelayVPN.exe")
            if (fileInRoot.exists()) {
                if (!fileInRoot.canExecute()) fileInRoot.setExecutable(true)
                fileInRoot.absolutePath
            } else {
                File(resourcesDir, "windows/MasterHttpRelayVPN.exe").absolutePath
            }
        }
        os.contains("mac") -> {
            val fileInRoot = File(resourcesDir, "MasterHttpRelayVPN")
            if (fileInRoot.exists()) {
                if (!fileInRoot.canExecute()) fileInRoot.setExecutable(true)
                fileInRoot.absolutePath
            } else {
                File(resourcesDir, "macos/MasterHttpRelayVPN").absolutePath
            }
        }
        else -> { // Linux / Unix
            val fileInRoot = File(resourcesDir, "MasterHttpRelayVPN")
            if (fileInRoot.exists()) {
                if (!fileInRoot.canExecute()) fileInRoot.setExecutable(true)
                fileInRoot.absolutePath
            } else {
                File(resourcesDir, "linux/MasterHttpRelayVPN").absolutePath
            }
        }
    }
}

