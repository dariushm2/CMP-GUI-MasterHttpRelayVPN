package com.darius.lionvpn.config

import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import com.darius.lionvpn.Constants
import com.darius.lionvpn.findResourcesDir
import com.darius.lionvpn.findRepoRoot
import com.darius.lionvpn.ui.model.SavedConfig

fun saveConfigLocally(deploymentId: String, authKey: String): Boolean {
    return try {
        val root = findResourcesDir()
        var exampleFile = File(root, Constants.Config.TEMPLATE_FILE_NAME)
        if (!exampleFile.exists()) {
            exampleFile = File(findRepoRoot(), Constants.Config.TEMPLATE_FILE_NAME)
        }
        ensureExampleFileReadOnly(exampleFile)

        val configFile = File(getUserDataDirectory(), Constants.Config.FILE_NAME)

        // READ existing config if it exists, otherwise read example config
        val fileToRead = if (configFile.exists()) configFile else exampleFile
        if (!fileToRead.exists()) {
            println("[Config JVM] ERROR: config file template not found!")
            return false
        }

        val content = fileToRead.readText(Charsets.UTF_8)
        
        // Parse current content and merge script fields
        val jsonMap = try {
            Json.parseToJsonElement(content).jsonObject.toMutableMap()
        } catch (e: Exception) {
            mutableMapOf()
        }
        
        jsonMap[Constants.Config.SCRIPT_ID] = JsonPrimitive(deploymentId)
        jsonMap[Constants.Config.AUTH_KEY] = JsonPrimitive(authKey)
        
        val finalObject = JsonObject(jsonMap)
        val prettyJson = Json { prettyPrint = true }
        val outputString = prettyJson.encodeToString(JsonObject.serializer(), finalObject)

        // Make sure parent directories exist
        configFile.parentFile?.mkdirs()
        configFile.writeText(outputString, Charsets.UTF_8)
        println("[Config JVM] Saved/updated config.json script_id and auth_key to: ${configFile.absolutePath}")
        true
    } catch (e: Exception) {
        println("[Config JVM] Error saving config: ${e.message}")
        false
    }
}

fun loadRawConfig(): String {
    return try {
        val configFile = File(getUserDataDirectory(), Constants.Config.FILE_NAME)
        
        var fileToRead = configFile
        if (!fileToRead.exists()) {
            val root = findResourcesDir()
            var exampleFile = File(root, Constants.Config.TEMPLATE_FILE_NAME)
            if (!exampleFile.exists()) {
                exampleFile = File(findRepoRoot(), Constants.Config.TEMPLATE_FILE_NAME)
            }
            ensureExampleFileReadOnly(exampleFile)
            
            // Create user's config.json from example template on first load
            try {
                getUserDataDirectory().mkdirs()
                exampleFile.copyTo(configFile, overwrite = true)
                println("[Config JVM] Initialized user config.json from template.")
                fileToRead = configFile
            } catch (copyEx: Exception) {
                println("[Config JVM] Failed to copy template config: ${copyEx.message}")
                fileToRead = exampleFile
            }
        }
        
        if (fileToRead.exists()) {
            fileToRead.readText(Charsets.UTF_8)
        } else {
            ""
        }
    } catch (e: Exception) {
        println("[Config JVM] Error loading raw config: ${e.message}")
        ""
    }
}

fun saveRawConfig(content: String): Boolean {
    return try {
        val configFile = File(getUserDataDirectory(), Constants.Config.FILE_NAME)
        
        configFile.parentFile?.mkdirs()
        configFile.writeText(content, Charsets.UTF_8)
        println("[Config JVM] Saved raw config.json to: ${configFile.absolutePath}")
        true
    } catch (e: Exception) {
        println("[Config JVM] Error saving raw config: ${e.message}")
        false
    }
}

fun loadDefaultConfigContent(): String {
    return try {
        val root = findResourcesDir()
        var exampleFile = File(root, Constants.Config.TEMPLATE_FILE_NAME)
        if (!exampleFile.exists()) {
            exampleFile = File(findRepoRoot(), Constants.Config.TEMPLATE_FILE_NAME)
        }
        ensureExampleFileReadOnly(exampleFile)
        
        if (exampleFile.exists()) {
            val content = exampleFile.readText(Charsets.UTF_8)
            
            // Load active script details to merge
            val configs = loadSavedScripts()
            val index = loadActiveScriptIndex()
            val active = if (index in configs.indices) configs[index] else null
            
            val deploymentId = active?.id ?: ""
            val authKey = active?.key ?: ""
            
            // Parse and merge script details
            val jsonMap = try {
                Json.parseToJsonElement(content).jsonObject.toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }
            
            jsonMap[Constants.Config.SCRIPT_ID] = JsonPrimitive(deploymentId)
            jsonMap[Constants.Config.AUTH_KEY] = JsonPrimitive(authKey)
            
            val finalObject = JsonObject(jsonMap)
            val prettyJson = Json { prettyPrint = true }
            prettyJson.encodeToString(JsonObject.serializer(), finalObject)
        } else {
            ""
        }
    } catch (e: Exception) {
        println("[Config JVM] Error loading default config: ${e.message}")
        ""
    }
}

private fun ensureExampleFileReadOnly(file: File) {
    try {
        if (file.exists() && file.canWrite()) {
            file.setWritable(false, false) // Set read-only for current and other users
            println("[Config JVM] Set ${file.name} to read-only.")
        }
    } catch (e: Exception) {
        println("[Config JVM] Could not set ${file.name} to read-only: ${e.message}")
    }
}
