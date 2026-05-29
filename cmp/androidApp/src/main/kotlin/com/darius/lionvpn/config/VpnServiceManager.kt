package com.darius.lionvpn.config

import android.content.Context
import android.content.Intent
import com.darius.lionvpn.ProxyService
import com.darius.lionvpn.Constants

class VpnServiceManager(
    private val context: Context,
    private val configTemplateProvider: ConfigTemplateProvider
) {
    fun startVpnService() {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val rawConfig = prefs.getString(Constants.PREF_RAW_CONFIG_JSON, "") ?: ""
        val currentId = prefs.getString(Constants.KEY_SCRIPT_ID, "") ?: ""
        val currentKey = prefs.getString(Constants.KEY_AUTH_KEY, "") ?: ""

        val configJson = rawConfig.ifBlank {
            val json = prefs.getString(Constants.PREF_SAVED_CONFIGS_JSON, null)
            val configs = if (!json.isNullOrBlank()) {
                try {
                    kotlinx.serialization.json.Json.decodeFromString<List<com.darius.lionvpn.ui.model.SavedConfig>>(json)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
            configTemplateProvider.generateDefaultJson(currentId, currentKey, configs)
        }

        val intent = Intent(context, ProxyService::class.java).apply {
            action = ProxyService.ACTION_START
            putExtra(ProxyService.EXTRA_CONFIG, configJson)
        }
        context.startForegroundService(intent)
    }

    fun stopVpnService() {
        val intent = Intent(context, ProxyService::class.java).apply {
            action = ProxyService.ACTION_STOP
        }
        context.startService(intent)
    }
}
