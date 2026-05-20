package com.darius.relay_vpn

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

fun loadSavedConfig(): Pair<String, String> {
    try {
        val resourcesDir = System.getProperty("compose.application.resources.dir")
            ?: "/home/dariush/Projects/CMP-GUI-MasterHttpRelayVPN/"

        val configFile = File(resourcesDir, "config.json")
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
    } catch (e: Exception) {
        println("[Config JVM] Error loading saved config: ${e.message}")
        e.printStackTrace()
    }
    return Pair("", "")
}

fun saveConfigLocally(deploymentId: String, authKey: String): Boolean {
    try {
        val resourcesDir = System.getProperty("compose.application.resources.dir")
            ?:"/home/dariush/Projects/CMP-GUI-MasterHttpRelayVPN/"

        val exampleFile = File(resourcesDir, "config.example.json")
        val configFile = File(resourcesDir, "config.json")
        
        // If config.example.json doesn't exist in resourcesDir, check fallback for dev mode
        val resolvedExampleFile = if (exampleFile.exists()) {
            exampleFile
        } else {
            File("/home/dariush/Projects/CMP-GUI-MasterHttpRelayVPN/config.example.json")
        }

        if (!resolvedExampleFile.exists()) {
            println("[Config JVM] ERROR: config.example.json not found!")
            return false
        }
        
        var content = resolvedExampleFile.readText(Charsets.UTF_8)
        
        // Safe drop-in replacements for the script ID and Auth Key placeholders
        content = content.replace("YOUR_APPS_SCRIPT_DEPLOYMENT_ID", deploymentId)
        content = content.replace("CHANGE_ME_TO_A_STRONG_SECRET", authKey)
        
        configFile.writeText(content, Charsets.UTF_8)
        println("[Config JVM] Saved new config.json to: ${configFile.absolutePath}")
        return true
    } catch (e: Exception) {
        println("[Config JVM] Error saving config: ${e.message}")
        e.printStackTrace()
        return false
    }
}

fun main() = application {
    initKoin()

    val (initialScriptId, initialAuthKey) = loadSavedConfig()

    Window(
        onCloseRequest = ::exitApplication,
        title = "HTTP Master Relay VPN",
    ) {
        currentWindowHolder.window = this.window
        val viewModel: AppViewModel = koinViewModel<AppViewModel>()
        App(
            connectivityHandler = koinInject(),
            initialScriptId = initialScriptId,
            initialAuthKey = initialAuthKey,
            onSaveConfig = { id, key ->
                saveConfigLocally(id, key)
            },
            onClick = { event ->
                viewModel.handleEvent(event)
            }
        )
    }
}

