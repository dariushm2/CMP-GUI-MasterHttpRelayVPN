package com.darius.lionvpn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.darius.lionvpn.ui.home.Event
import org.koin.android.ext.android.getKoin
import org.koin.compose.koinInject
import timber.log.Timber
import android.widget.Toast
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : ComponentActivity() {

    private var onCertSaved: (() -> Unit)? = null
    private var resolvedCaCertFile: File? = null

    private val saveCertLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        if (uri != null) {
            val caCertFile = resolvedCaCertFile ?: File(cacheDir, "ca/ca.crt")
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    caCertFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                ProxyService.addLogLine("CA Certificate saved successfully to storage.")
                Toast.makeText(this, "Certificate saved successfully!", Toast.LENGTH_SHORT).show()
                onCertSaved?.invoke()
            } catch (e: Exception) {
                Timber.e(e, "Failed to save certificate file")
                ProxyService.addLogLine("Error saving certificate: ${e.message}")
                Toast.makeText(this, "Failed to save certificate: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            ProxyService.addLogLine("Certificate saving cancelled by user.")
        }
    }

    private val vpnPrepareLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startVpnService()
        } else {
            ProxyService.addLogLine("VPN permission denied by user.")
        }
    }

    private fun startVpnService() {
        val prefs = getSharedPreferences("vpn_config", Context.MODE_PRIVATE)
        val currentId = prefs.getString("script_id", "") ?: ""
        val currentKey = prefs.getString("auth_key", "") ?: ""
        
        // Create standard JSON config dictionary used by ProxyServer
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getKoin().get<ContextFactory>().attach(this)

        setContent {
            val data = this.intent.data
            data?.toString()?.let {
                DeepLinkHandler.setDeepLink(it)
            }

            // This will make sure the icons in status bar adapt to the theme
            val view = LocalView.current
            val darkTheme = isSystemInDarkTheme()
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            // SharedPreferences config storage
            val prefs = remember { getSharedPreferences("vpn_config", Context.MODE_PRIVATE) }
            val initialScriptId = remember { prefs.getString("script_id", "") ?: "" }
            val initialAuthKey = remember { prefs.getString("auth_key", "") ?: "" }

            val isVpnRunning by ProxyService.isVpnRunning.collectAsState()
            val vpnLogs by ProxyService.vpnLogs.collectAsState()

            var showInstructionsDialog by remember { mutableStateOf(false) }
            onCertSaved = {
                showInstructionsDialog = true
            }

            App(
                connectivityHandler = koinInject(),
                initialScriptId = initialScriptId,
                initialAuthKey = initialAuthKey,
                isVpnRunning = isVpnRunning,
                onSaveConfig = { id, key ->
                    prefs.edit().apply {
                        putString("script_id", id)
                        putString("auth_key", key)
                        apply()
                    }
                    Timber.i("Saved config: scriptId=$id")
                },
                onClick = { event ->
                    when (event) {
                        is Event.Connect -> {
                            if (isVpnRunning) {
                                val intent = Intent(this, ProxyService::class.java).apply {
                                    action = ProxyService.ACTION_STOP
                                }
                                startService(intent)
                            } else {
                                val vpnIntent = VpnService.prepare(this)
                                if (vpnIntent != null) {
                                    vpnPrepareLauncher.launch(vpnIntent)
                                } else {
                                    startVpnService()
                                }
                            }
                        }
                        is Event.Certificate -> {
                            Timber.i("Certificate action clicked")
                            // Dynamically query Python for the exact certificate path to prevent any cache/temp folder mismatch
                            val caCertFile = try {
                                if (!Python.isStarted()) {
                                    Python.start(AndroidPlatform(applicationContext))
                                }
                                val py = Python.getInstance()
                                // Pre-load the android_entry module to ensure that our synced "src" directory is added to sys.path
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
                                    File(cacheDir, "ca/ca.crt")
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error retrieving cert path from Python")
                                File(cacheDir, "ca/ca.crt")
                            }

                            Timber.i("Resolved CA Cert path: ${caCertFile.absolutePath}")
                            ProxyService.addLogLine("Searching for CA certificate at: ${caCertFile.absolutePath}")
                            resolvedCaCertFile = caCertFile

                            if (!caCertFile.exists()) {
                                ProxyService.addLogLine("Error: CA certificate was not found at ${caCertFile.absolutePath}. Please connect to the VPN at least once to start the proxy and generate the certificate.")
                                Toast.makeText(this@MainActivity, "Please connect to the VPN at least once to generate the certificate.", Toast.LENGTH_LONG).show()
                            } else {
                                saveCertLauncher.launch("lion_vpn_ca.crt")
                            }
                        }
                    }
                },
                log = if (isDebugBuild()) vpnLogs else null
            )

            if (showInstructionsDialog) {
                AlertDialog(
                    onDismissRequest = { showInstructionsDialog = false },
                    title = {
                        Text(
                            text = "🔒 Install HTTPS CA Certificate",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "To successfully intercept and relay HTTPS traffic, you must install the generated CA certificate as a trusted credential on your device.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Please follow these manual steps precisely:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            
                            val steps = listOf(
                                "1. Locate the file: The certificate has been saved to your storage as 'lion_vpn_ca.crt'.",
                                "2. Open security settings: Tap the 'Open Security Settings' button below.",
                                "3. Go to credentials: Find 'More security settings' -> 'Encryption & credentials' (or 'Install from device storage').",
                                "4. Choose CA certificate: Tap 'Install a certificate' -> 'CA certificate'.",
                                "5. Confirm warning: Tap 'Install anyway' on the security warning.",
                                "6. Select the file: Locate and select 'lion_vpn_ca.crt' from your saved folder."
                            )
                            
                            steps.forEach { step ->
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showInstructionsDialog = false
                                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                                try {
                                    startActivity(intent)
                                } catch (e: Exception) {
                                    try {
                                        startActivity(Intent(Settings.ACTION_SETTINGS))
                                    } catch (ex: Exception) {
                                        ProxyService.addLogLine("Error opening Settings: ${ex.message}")
                                        Toast.makeText(this@MainActivity, "Could not open system settings automatically.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        ) {
                            Text("Open Security Settings", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showInstructionsDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}
