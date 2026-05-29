package com.darius.lionvpn.config

import android.content.Context
import android.content.Intent
import com.darius.lionvpn.Constants
import com.darius.lionvpn.ProxyService

class VpnServiceManager(
    private val context: Context,
    private val configTemplateProvider: ConfigTemplateProvider
) {
    fun startVpnService() {
        val prefs = context.getSharedPreferences(Constants.Prefs.NAME, Context.MODE_PRIVATE)
        val rawConfig = prefs.getString(Constants.Prefs.KEY_RAW_CONFIG_JSON, "") ?: ""
        val currentId = prefs.getString(Constants.Prefs.KEY_SCRIPT_ID, "") ?: ""
        val currentKey = prefs.getString(Constants.Prefs.KEY_AUTH_KEY, "") ?: ""

        val configJson = rawConfig.ifBlank {
            configTemplateProvider.generateDefaultJson(currentId, currentKey)
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
