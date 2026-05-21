package com.darius.lionvpn

import kotlinx.io.IOException
import java.io.File

fun loadSavedConfig(): Pair<String, String> {
    return try {
        val binaryPath = getPythonExecutablePath()
        val binaryFile = File(binaryPath)
        val configFile = if (binaryFile.parentFile != null) {
            File(binaryFile.parentFile, "config.json")
        } else {
            File(findResourcesDir(), "config.json")
        }

        if (configFile.exists()) {
            val content = configFile.readText(Charsets.UTF_8)

            // Clean extraction of script_id and auth_key values using simple regex
            val scriptIdRegex = "\"script_id\"\\s*:\\s*\"([^\"]+)\"".toRegex()
            val authKeyRegex = "\"auth_key\"\\s*:\\s*\"([^\"]+)\"".toRegex()

            val scriptId = scriptIdRegex.find(content)?.groupValues?.get(1) ?: ""
            val authKey = authKeyRegex.find(content)?.groupValues?.get(1) ?: ""

            // Filter out default placeholder values to keep text inputs clean
            val filteredScriptId = if (scriptId == "YOUR_APPS_SCRIPT_DEPLOYMENT_ID") "" else scriptId
            val filteredAuthKey = if (authKey == "CHANGE_ME_TO_A_STRONG_SECRET") "" else authKey

            return Pair(filteredScriptId, filteredAuthKey)
        }
        throw IOException("File does not exist")
    } catch (e: IOException) {
        println("[Config JVM] Error loading saved config: ${e.message}")
        Pair("", "")
    }
}

fun saveConfigLocally(deploymentId: String, authKey: String): Boolean {
    return try {
        val resourcesDir = findResourcesDir()
        val exampleFile = File(resourcesDir, "config.example.json")

        val binaryPath = getPythonExecutablePath()
        val binaryFile = File(binaryPath)

        // Write config.json in the exact same platform-specific directory containing the python executable
        val configFile = if (binaryFile.parentFile != null) {
            File(binaryFile.parentFile, "config.json")
        } else {
            File(resourcesDir, "config.json")
        }

        // If config.example.json doesn't exist in resourcesDir, check fallback for dev mode
        val resolvedExampleFile = if (exampleFile.exists()) {
            exampleFile
        } else {
            File(findRepoRoot(), "config.example.json")
        }

        if (!resolvedExampleFile.exists()) {
            println("[Config JVM] ERROR: config.example.json not found!")
            return false
        }

        var content = resolvedExampleFile.readText(Charsets.UTF_8)

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
