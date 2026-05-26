package com.darius.lionvpn.config

import android.content.Context
import android.content.Intent
import com.darius.lionvpn.ProxyService

class VpnServiceManager(
    private val context: Context,
    private val configTemplateProvider: ConfigTemplateProvider
) {
    fun startVpnService() {
        val prefs = context.getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
        val rawConfig = prefs.getString("raw_config_json", "") ?: ""
        val currentId = prefs.getString("script_id", "") ?: ""
        val currentKey = prefs.getString("auth_key", "") ?: ""

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
