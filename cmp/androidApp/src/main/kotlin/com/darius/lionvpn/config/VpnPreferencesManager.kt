package com.darius.lionvpn.config

import android.content.Context
import androidx.core.content.edit
import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.model.SavedConfig
import kotlinx.serialization.json.Json
import timber.log.Timber
import com.darius.lionvpn.Constants

class VpnPreferencesManager(private val context: Context) {

    fun loadConfigsFromPrefs(): List<SavedConfig> {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(Constants.PREF_SAVED_CONFIGS_JSON, null)
        if (json.isNullOrBlank()) {
            val legacyId = prefs.getString(Constants.KEY_SCRIPT_ID, "") ?: ""
            val legacyKey = prefs.getString(Constants.KEY_AUTH_KEY, "") ?: ""
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
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val index = prefs.getInt(Constants.PREF_SELECTED_CONFIG_INDEX, -1)
        if (index == -1) {
            val legacyId = prefs.getString(Constants.KEY_SCRIPT_ID, "") ?: ""
            val legacyKey = prefs.getString(Constants.KEY_AUTH_KEY, "") ?: ""
            if (legacyId.isNotBlank() && legacyKey.isNotBlank()) {
                return 0
            }
        }
        return index
    }

    fun saveConfigsToPrefs(configs: List<SavedConfig>, index: Int) {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val json = Json.encodeToString(configs)
        prefs.edit {
            putString(Constants.PREF_SAVED_CONFIGS_JSON, json)
            putInt(Constants.PREF_SELECTED_CONFIG_INDEX, index)

            if (index in configs.indices) {
                val active = configs[index]
                putString(Constants.KEY_SCRIPT_ID, active.id)
                putString(Constants.KEY_AUTH_KEY, active.key)
            } else {
                putString(Constants.KEY_SCRIPT_ID, "")
                putString(Constants.KEY_AUTH_KEY, "")
            }
        }
    }

    fun loadRawConfigFromPrefs(): String {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(Constants.PREF_RAW_CONFIG_JSON, "") ?: ""
    }

    fun loadLanguageFromPrefs(): Lang {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val langStr = prefs.getString(Constants.PREF_LANGUAGE, Lang.FA.name) ?: Lang.FA.name
        return try {
            Lang.valueOf(langStr)
        } catch (e: Exception) {
            Lang.FA
        }
    }

    fun saveSettingsToPrefs(rawConfig: String, lang: Lang) {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(Constants.PREF_RAW_CONFIG_JSON, rawConfig)
            putString(Constants.PREF_LANGUAGE, lang.name)
        }
    }
}
