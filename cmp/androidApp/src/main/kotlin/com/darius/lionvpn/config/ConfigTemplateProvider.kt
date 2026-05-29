package com.darius.lionvpn.config

import android.content.Context
import com.darius.lionvpn.R
import kotlinx.serialization.json.*
import com.darius.lionvpn.ui.model.SavedConfig
import com.darius.lionvpn.Constants

class ConfigTemplateProvider(private val context: Context) {

    fun loadTemplateJson(): String {
        return try {
            context.resources.openRawResource(R.raw.config_example).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to load config_example.json template from raw resources", e)
        }
    }

    fun generateDefaultJson(id: String, key: String, configs: List<SavedConfig> = emptyList()): String {
        val template = loadTemplateJson()
        return try {
            val jsonMap = Json.parseToJsonElement(template).jsonObject.toMutableMap()
            jsonMap[Constants.KEY_SCRIPT_ID] = JsonPrimitive(id)
            jsonMap[Constants.KEY_AUTH_KEY] = JsonPrimitive(key)
            
            val idsArray = configs.map { config ->
                JsonObject(mapOf(
                    Constants.KEY_SCRIPT_ID to JsonPrimitive(config.id),
                    Constants.KEY_AUTH_KEY to JsonPrimitive(config.key)
                ))
            }
            jsonMap[Constants.KEY_SCRIPT_IDS] = JsonArray(idsArray)
            
            val prettyJson = Json { prettyPrint = true }
            prettyJson.encodeToString(JsonObject.serializer(), JsonObject(jsonMap))
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse config template", e)
        }
    }
}
