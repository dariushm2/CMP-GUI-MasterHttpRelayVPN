package com.darius.lionvpn.config

import android.content.Context
import androidx.core.content.edit
import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.model.SavedConfig
import kotlinx.serialization.json.Json
import timber.log.Timber

class VpnPreferencesManager(private val context: Context) {

    fun loadConfigsFromPrefs(): List<SavedConfig> {
        val prefs = context.getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
        val json = prefs.getString("saved_configs_json", null)
        if (json.isNullOrBlank()) {
            val legacyId = prefs.getString("script_id", "") ?: ""
            val legacyKey = prefs.getString("auth_key", "") ?: ""
            if (legacyId.isNotBlank() && legacyKey.isNotBlank()) {
                return listOf(SavedConfig(id = legacyId, key = legacyKey, name = "Default Config"))
            }
            return emptyList()
        }
        return try {
            Json.decodeFromString<List<SavedConfig>>(json)
        } catch (e: Exception) {
            Timber.e(e, "Failed to decode saved configs")
            emptyList()
        }
    }

    fun loadSelectedIndexFromPrefs(): Int {
        val prefs = context.getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
        val index = prefs.getInt("selected_config_index", -1)
        if (index == -1) {
            val legacyId = prefs.getString("script_id", "") ?: ""
            val legacyKey = prefs.getString("auth_key", "") ?: ""
            if (legacyId.isNotBlank() && legacyKey.isNotBlank()) {
                return 0
            }
        }
        return index
    }

    fun saveConfigsToPrefs(configs: List<SavedConfig>, index: Int) {
        val prefs = context.getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
        val json = Json.encodeToString(configs)
        prefs.edit {
            putString("saved_configs_json", json)
            putInt("selected_config_index", index)

            if (index in configs.indices) {
                val active = configs[index]
                putString("script_id", active.id)
                putString("auth_key", active.key)
            } else {
                putString("script_id", "")
                putString("auth_key", "")
            }
        }
    }

    fun loadRawConfigFromPrefs(): String {
        val prefs = context.getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
        return prefs.getString("raw_config_json", "") ?: ""
    }

    fun loadLanguageFromPrefs(): Lang {
        val prefs = context.getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
        val langStr = prefs.getString("language", "FA") ?: "FA"
        return try {
            Lang.valueOf(langStr)
        } catch (e: Exception) {
            Lang.FA
        }
    }

    fun saveSettingsToPrefs(rawConfig: String, lang: Lang) {
        val prefs = context.getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
        prefs.edit {
            putString("raw_config_json", rawConfig)
            putString("language", lang.name)
        }
    }
}
