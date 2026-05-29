package com.darius.lionvpn.config

import android.content.Context
import com.darius.lionvpn.R
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

class ConfigTemplateProvider(private val context: Context) {

    fun loadTemplateJson(): String {
        return try {
            context.resources.openRawResource(R.raw.config_example).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to load config_example.json template from raw resources", e)
        }
    }

    fun generateDefaultJson(id: String, key: String): String {
        val template = loadTemplateJson()
        return try {
            val jsonMap = Json.parseToJsonElement(template).jsonObject.toMutableMap()
            jsonMap["script_id"] = JsonPrimitive(id)
            jsonMap["auth_key"] = JsonPrimitive(key)
            val prettyJson = Json { prettyPrint = true }
            prettyJson.encodeToString(JsonObject.serializer(), JsonObject(jsonMap))
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse config template", e)
        }
    }
}
