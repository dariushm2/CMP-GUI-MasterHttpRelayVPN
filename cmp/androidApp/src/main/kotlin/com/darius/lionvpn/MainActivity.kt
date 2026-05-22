package com.darius.lionvpn

import android.app.Activity
import android.net.VpnService
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.koinInject
import java.io.File

class MainActivity : ComponentActivity() {

    private var resolvedCaCertFile: File? = null
    private val vm: AndroidAppViewModel by viewModel()

    private val saveCertLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        if (uri != null) {
            val caCertFile = resolvedCaCertFile ?: File(cacheDir, "ca/ca.crt")
            saveCertificateUri(uri, caCertFile)
            vm.onCertSavedSuccess()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getKoin().get<ContextFactory>().attach(this)

        // Load initial configs from SharedPreferences reactively on startup
        vm.initializeConfigs(loadConfigsFromPrefs(), loadSelectedIndexFromPrefs())

        // Observe flow modifications reactively and save back to SharedPreferences
        lifecycleScope.launch {
            combine(vm.savedConfigs, vm.selectedConfigIndex) { configs, index ->
                Pair(configs, index)
            }.collect { (configs, index) ->
                withContext(Dispatchers.IO) {
                    saveConfigsToPrefs(configs, index)
                }
            }
        }

        // Listen for UI effects emitted by the pure parameterless ViewModel
        lifecycleScope.launch {
            vm.uiEffect.collect { effect ->
                when (effect) {
                    is AndroidUiEffect.ConnectVpn -> {
                        val isRunning = ProxyService.isVpnRunning.value
                        if (isRunning) {
                            stopVpnService()
                        } else {
                            val vpnIntent = VpnService.prepare(this@MainActivity)
                            if (vpnIntent != null) {
                                vpnPrepareLauncher.launch(vpnIntent)
                            } else {
                                startVpnService()
                            }
                        }
                    }
                    is AndroidUiEffect.CheckAndSaveCertificate -> {
                        checkAndGenerateCertificate(lifecycleScope) { caCertFile ->
                            resolvedCaCertFile = caCertFile
                            ProxyService.addLogLine("Searching for CA certificate at: ${caCertFile.absolutePath}")
                            if (!caCertFile.exists()) {
                                ProxyService.addLogLine("Error: CA certificate was not found at ${caCertFile.absolutePath}. Please connect to the VPN at least once to start the proxy and generate the certificate.")
                                Toast.makeText(this@MainActivity, "Please connect to the VPN at least once to generate the certificate.", Toast.LENGTH_LONG).show()
                            } else {
                                saveCertLauncher.launch("lion_vpn_ca.crt")
                            }
                        }
                    }
                }
            }
        }

        setContent {
            val data = this.intent.data
            data?.toString()?.let {
                DeepLinkHandler.setDeepLink(it)
            }

            val view = LocalView.current
            val darkTheme = isSystemInDarkTheme()
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            val homeState by vm.homeState.collectAsState()
            val showInstructionsDialog by vm.showInstructionsDialog.collectAsState()

            App(
                connectivityHandler = koinInject(),
                state = homeState,
                onClick = { event ->
                    vm.handleEvent(event)
                }
            )

            if (showInstructionsDialog) {
                CertInstructionsDialog(
                    onDismiss = { vm.setInstructionsDialogVisible(false) }
                )
            }
        }
    }
}
