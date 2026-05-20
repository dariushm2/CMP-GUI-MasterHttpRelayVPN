package com.darius.relay_vpn

import java.io.File

fun getPythonExecutablePath(): String {
    val resourcesDir = System.getProperty("compose.application.resources.dir")
        ?: "/home/dariush/Projects/CMP-GUI-MasterHttpRelayVPN/cmp/desktopApp/src/main/resources"

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

