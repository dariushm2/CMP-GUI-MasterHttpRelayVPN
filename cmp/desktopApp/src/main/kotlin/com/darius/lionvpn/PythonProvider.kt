package com.darius.lionvpn

import kotlinx.io.files.FileNotFoundException
import java.io.File

fun findRepoRoot(): File {
    val dir = File(System.getProperty("user.dir")).parentFile
    return if (File(dir, Constants.Config.TEMPLATE_FILE_NAME).exists()) dir
    else throw FileNotFoundException()
}

fun findResourcesDir(): File {
    val resourcesProp = System.getProperty("compose.application.resources.dir")
    if (resourcesProp != null) {
        return File(resourcesProp)
    }
    // Development mode fallback
    val repoRoot = findRepoRoot()
    val os = System.getProperty("os.name").lowercase()
    val platformDirName = when {
        os.contains("win") -> "windows"
        os.contains("mac") -> "macos"
        else -> "linux"
    }
    val devResources = File(repoRoot, "cmp/desktopApp/src/$platformDirName/resources")
    return if (devResources.exists()) devResources
    else File(System.getProperty("user.dir"), "src/$platformDirName/resources")
}

fun getPythonExecutablePath(): String {
    val resourcesDir = findResourcesDir()
    val binName = if (System.getProperty("os.name").lowercase().contains("win")) {
        "MasterHttpRelayVPN.exe"
    } else {
        "MasterHttpRelayVPN"
    }
    val file = File(resourcesDir, binName)
    if (file.exists() && !file.canExecute()) {
        file.setExecutable(true)
    }
    return file.absolutePath
}
