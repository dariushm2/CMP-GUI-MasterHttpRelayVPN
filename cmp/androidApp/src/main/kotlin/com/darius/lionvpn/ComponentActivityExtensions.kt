package com.darius.lionvpn

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import com.darius.lionvpn.ui.model.SavedConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import androidx.core.content.edit

fun ComponentActivity.startVpnService() {
    val prefs = getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
    val currentId = prefs.getString("script_id", "") ?: ""
    val currentKey = prefs.getString("auth_key", "") ?: ""

    val configJson = """
        {
            "listen_host": "127.0.0.1",
            "http_port": 8085,
            "socks5_port": 1080,
            "script_id": "$currentId",
            "auth_key": "$currentKey",
            "google_ip": "216.239.38.120",
            "front_domain": "www.google.com",
            "log_level": "INFO",
            "verify_ssl": true,
            "lan_sharing": false,
            "relay_timeout": 25,
            "tls_connect_timeout": 15,
            "tcp_connect_timeout": 10,
            "direct_hosts": [],
            "hosts": {}
        }
    """.trimIndent()

    val intent = Intent(this, ProxyService::class.java).apply {
        action = ProxyService.ACTION_START
        putExtra(ProxyService.EXTRA_CONFIG, configJson)
    }
    startForegroundService(intent)
}

fun ComponentActivity.stopVpnService() {
    val intent = Intent(this, ProxyService::class.java).apply {
        action = ProxyService.ACTION_STOP
    }
    startService(intent)
}

fun ComponentActivity.loadConfigsFromPrefs(): List<SavedConfig> {
    val prefs = getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
    val json = prefs.getString("saved_configs_json", null)
    if (json.isNullOrBlank()) {
        // Migration fallback: if legacy single configuration is present, form a default config profile
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

fun ComponentActivity.loadSelectedIndexFromPrefs(): Int {
    val prefs = getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
    val index = prefs.getInt("selected_config_index", -1)
    if (index == -1) {
        // Migration fallback: if legacy configuration is present, select the defaulted index 0
        val legacyId = prefs.getString("script_id", "") ?: ""
        val legacyKey = prefs.getString("auth_key", "") ?: ""
        if (legacyId.isNotBlank() && legacyKey.isNotBlank()) {
            return 0
        }
    }
    return index
}

fun ComponentActivity.saveConfigsToPrefs(configs: List<SavedConfig>, index: Int) {
    val prefs = getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
    val json = Json.encodeToString(configs)
    prefs.edit {
        putString("saved_configs_json", json)
        putInt("selected_config_index", index)

        // Maintain standard single-config keys for the foreground ProxyService to load the active script
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

fun ComponentActivity.checkAndGenerateCertificate(
    lifecycleScope: LifecycleCoroutineScope,
    onResult: (File) -> Unit
) {
    lifecycleScope.launch(Dispatchers.IO) {
        val context = this@checkAndGenerateCertificate
        val caCertFile = try {
            if (!com.chaquo.python.Python.isStarted()) {
                com.chaquo.python.Python.start(com.chaquo.python.android.AndroidPlatform(context.applicationContext))
            }
            val py = com.chaquo.python.Python.getInstance()
            try {
                py.getModule("android_entry")
            } catch (e: Exception) {
                Timber.w(e, "Error pre-loading android_entry module")
            }
            val mitmModule = py.getModule("proxy.mitm")
            val path = mitmModule.get("CA_CERT_FILE")?.toString()
            if (path != null) {
                val file = File(path)
                if (!file.exists()) {
                    ProxyService.addLogLine("CA certificate not found. Generating it on-demand...")
                    try {
                        mitmModule.callAttr("MITMCertManager")
                        ProxyService.addLogLine("CA certificate generated successfully on-demand at: ${file.absolutePath}")
                    } catch (genEx: Exception) {
                        Timber.e(genEx, "Failed to generate CA certificate on-demand")
                        ProxyService.addLogLine("Error generating CA certificate: ${genEx.message}")
                    }
                }
                file
            } else {
                File(context.cacheDir, "ca/ca.crt")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving cert path from Python")
            File(context.cacheDir, "ca/ca.crt")
        }

        withContext(Dispatchers.Main) {
            onResult(caCertFile)
        }
    }
}

fun ComponentActivity.saveCertificateUri(uri: android.net.Uri, certFile: File) {
    try {
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            certFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        ProxyService.addLogLine("CA Certificate saved successfully to storage.")
        Toast.makeText(this, "Certificate saved successfully!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Timber.e(e, "Failed to save certificate file")
        ProxyService.addLogLine("Error saving certificate: ${e.message}")
        Toast.makeText(this, "Failed to save certificate: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
