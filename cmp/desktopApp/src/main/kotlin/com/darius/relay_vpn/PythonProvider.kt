package com.darius.relay_vpn

import java.io.File

fun findRepoRoot(): File {
    var dir = File(System.getProperty("user.dir")).absoluteFile
    for (i in 0..4) {
        if (File(dir, "config.example.json").exists()) {
            return dir
        }
        dir = dir.parentFile ?: break
    }
    return File(System.getProperty("user.dir")).absoluteFile
}

fun findResourcesDir(): File {
    val resourcesProp = System.getProperty("compose.application.resources.dir")
    if (resourcesProp != null) {
        return File(resourcesProp)
    }
    val repoRoot = findRepoRoot()
    val devResources = File(repoRoot, "cmp/desktopApp/src/main/resources")
    if (devResources.exists()) {
        return devResources
    }
    return File(System.getProperty("user.dir"), "src/main/resources")
}

fun getPythonExecutablePath(): String {
    val resourcesDir = findResourcesDir()
    val os = System.getProperty("os.name").lowercase()
    
    return when {
        os.contains("win") -> {
            val fileInRoot = File(resourcesDir, "MasterHttpRelayVPN.exe")
            if (fileInRoot.exists()) {
                fileInRoot.absolutePath
            } else {
                File(resourcesDir, "windows/MasterHttpRelayVPN.exe").absolutePath
            }
        }
        os.contains("mac") -> {
            val fileInRoot = File(resourcesDir, "MasterHttpRelayVPN")
            if (fileInRoot.exists()) {
                fileInRoot.absolutePath
            } else {
                File(resourcesDir, "macos/MasterHttpRelayVPN").absolutePath
            }
        }
        else -> { // Linux / Unix
            val fileInRoot = File(resourcesDir, "MasterHttpRelayVPN")
            if (fileInRoot.exists()) {
                fileInRoot.absolutePath
            } else {
                File(resourcesDir, "linux/MasterHttpRelayVPN").absolutePath
            }
        }
    }
}

