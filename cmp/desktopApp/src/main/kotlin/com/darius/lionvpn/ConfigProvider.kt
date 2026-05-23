package com.darius.lionvpn

import kotlinx.io.IOException
import java.io.File
import kotlinx.serialization.json.Json
import com.darius.lionvpn.ui.model.SavedConfig

fun saveConfigLocally(deploymentId: String, authKey: String): Boolean {
    return try {
        val root = findResourcesDir()
        var exampleFile = File(root, "config.example.json")
        if (!exampleFile.exists()) {
            exampleFile = File(findRepoRoot(), "config.example.json")
        }

        val binaryPath = getPythonExecutablePath()
        val binaryFile = File(binaryPath)

        val configFile = File(binaryFile.parentFile, "config.json")

        if (!exampleFile.exists()) {
            println("[Config JVM] ERROR: config.example.json not found!")
            return false
        }

        var content = exampleFile.readText(Charsets.UTF_8)

        // Safe drop-in replacements for the script ID and Auth Key placeholders
        content = content.replace("YOUR_APPS_SCRIPT_DEPLOYMENT_ID", deploymentId)
        content = content.replace("CHANGE_ME_TO_A_STRONG_SECRET", authKey)

        // Make sure parent directories exist
        configFile.parentFile?.mkdirs()

        configFile.writeText(content, Charsets.UTF_8)
        println("[Config JVM] Saved new config.json to: ${configFile.absolutePath}")
        true
    } catch (e: IOException) {
        println("[Config JVM] Error saving config: ${e.message}")
        false
    }
}

fun loadSavedScripts(): List<SavedConfig> {
    return try {
        val file = File(getSavedScriptsFilePath())
        if (file.exists()) {
            val content = file.readText(Charsets.UTF_8)
            Json.decodeFromString<List<SavedConfig>>(content)
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        println("[Config JVM] Error loading saved scripts: ${e.message}")
        emptyList()
    }
}

fun saveSavedScripts(scripts: List<SavedConfig>): Boolean {
    return try {
        val file = File(getSavedScriptsFilePath())
        file.parentFile?.mkdirs()
        val content = Json.encodeToString(scripts)
        file.writeText(content, Charsets.UTF_8)
        true
    } catch (e: Exception) {
        println("[Config JVM] Error saving scripts: ${e.message}")
        false
    }
}

fun loadActiveScriptIndex(): Int {
    return try {
        val file = File(getActiveScriptIndexFilePath())
        if (file.exists()) {
            file.readText(Charsets.UTF_8).trim().toIntOrNull() ?: -1
        } else {
            -1
        }
    } catch (e: Exception) {
        -1
    }
}

fun saveActiveScriptIndex(index: Int): Boolean {
    return try {
        val file = File(getActiveScriptIndexFilePath())
        file.parentFile?.mkdirs()
        file.writeText(index.toString(), Charsets.UTF_8)
        true
    } catch (e: Exception) {
        false
    }
}

private fun getSavedScriptsFilePath(): String {
    val binaryPath = getPythonExecutablePath()
    val binaryFile = File(binaryPath)
    return if (binaryFile.parentFile != null) {
        File(binaryFile.parentFile, "saved_scripts.json").absolutePath
    } else {
        File(findResourcesDir(), "saved_scripts.json").absolutePath
    }
}

private fun getActiveScriptIndexFilePath(): String {
    val binaryPath = getPythonExecutablePath()
    val binaryFile = File(binaryPath)
    return if (binaryFile.parentFile != null) {
        File(binaryFile.parentFile, "active_script_index.txt").absolutePath
    } else {
        File(findResourcesDir(), "active_script_index.txt").absolutePath
    }
}
