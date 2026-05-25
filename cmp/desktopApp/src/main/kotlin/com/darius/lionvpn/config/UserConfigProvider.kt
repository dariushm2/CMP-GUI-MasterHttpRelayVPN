package com.darius.lionvpn.config

import java.io.File
import kotlinx.serialization.json.Json
import com.darius.lionvpn.JvmPlatform
import com.darius.lionvpn.ui.model.SavedConfig

fun loadConf(): LionVpnConf {
    return try {
        val file = File(getUserDataDirectory(), "lionvpn.conf")
        if (file.exists()) {
            val content = file.readText(Charsets.UTF_8)
            Json.decodeFromString<LionVpnConf>(content)
        } else {
            LionVpnConf()
        }
    } catch (e: Exception) {
        println("[Config JVM] Error loading lionvpn.conf: ${e.message}")
        LionVpnConf()
    }
}

fun saveConf(conf: LionVpnConf): Boolean {
    return try {
        val file = File(getUserDataDirectory(), "lionvpn.conf")
        file.parentFile?.mkdirs()
        val prettyJson = Json { prettyPrint = true }
        val content = prettyJson.encodeToString(LionVpnConf.serializer(), conf)
        file.writeText(content, Charsets.UTF_8)
        true
    } catch (e: Exception) {
        println("[Config JVM] Error saving lionvpn.conf: ${e.message}")
        false
    }
}

fun loadSavedScripts(): List<SavedConfig> {
    return loadConf().savedConfigs
}

fun saveSavedScripts(scripts: List<SavedConfig>): Boolean {
    val current = loadConf()
    return saveConf(current.copy(savedConfigs = scripts))
}

fun loadActiveScriptIndex(): Int {
    return loadConf().selectedConfigIndex
}

fun saveActiveScriptIndex(index: Int): Boolean {
    val current = loadConf()
    return saveConf(current.copy(selectedConfigIndex = index))
}

fun loadLanguagePreference(): String {
    return loadConf().language
}

fun saveLanguagePreference(language: String): Boolean {
    val current = loadConf()
    return saveConf(current.copy(language = language))
}

fun getUserDataDirectory(): File {
    val jvmPlatform = JvmPlatform()
    val homeDir = System.getProperty("user.home")
    val appDirName = "LionVPN"
    
    val dir = when (jvmPlatform.os) {
        JvmPlatform.OS.WIN -> {
            val appData = System.getenv("APPDATA")
            if (appData != null) File(appData, appDirName) else File(homeDir, "AppData/Roaming/$appDirName")
        }
        JvmPlatform.OS.MAC -> {
            File(homeDir, "Library/Application Support/$appDirName")
        }
        JvmPlatform.OS.LINUX -> {
            val xdgConfig = System.getenv("XDG_CONFIG_HOME")
            if (xdgConfig != null) File(xdgConfig, appDirName.lowercase()) else File(homeDir, ".config/${appDirName.lowercase()}")
        }
    }
    
    if (!dir.exists()) {
        dir.mkdirs()
    }

    return dir
}
