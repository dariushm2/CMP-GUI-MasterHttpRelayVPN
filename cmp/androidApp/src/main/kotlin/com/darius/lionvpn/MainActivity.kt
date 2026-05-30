package com.darius.lionvpn

import android.content.Context
import android.net.VpnService
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.koinInject
import java.io.File
import android.content.Intent
import android.provider.Settings
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.config.*
import com.darius.lionvpn.model.AndroidUiEffect

class MainActivity : ComponentActivity() {

    private var resolvedCaCertFile: File? = null
    private val vm: AndroidAppViewModel by viewModel()
    private val configTemplateProvider: ConfigTemplateProvider by inject()
    private val vpnPreferencesManager: VpnPreferencesManager by inject()
    private val vpnServiceManager: VpnServiceManager by inject()
    private val vpnCertificateManager: VpnCertificateManager by inject()
    private val vpnLanguageManager: VpnLanguageManager by inject()

    private val saveCertLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        if (uri != null) {
            val caCertFile = resolvedCaCertFile ?: File(cacheDir, "ca/ca.crt")
            vpnCertificateManager.saveCertificateUri(uri, caCertFile)
            vm.onCertSavedSuccess()
        } else {
            ProxyService.addLogLine("Certificate saving cancelled by user.")
        }
    }

    private val vpnPrepareLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            vpnServiceManager.startVpnService()
        } else {
            ProxyService.addLogLine("VPN permission denied by user.")
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val languageManager = VpnLanguageManager(VpnPreferencesManager(newBase))
        super.attachBaseContext(languageManager.applyLocaleToContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getKoin().get<ContextFactory>().attach(this)

        // Load initial configs from SharedPreferences reactively on startup
        val configs = vpnPreferencesManager.loadConfigsFromPrefs()
        val selectedIndex = vpnPreferencesManager.loadSelectedIndexFromPrefs()
        val rawConfig = vpnPreferencesManager.loadRawConfigFromPrefs()
        val lang = vpnPreferencesManager.loadLanguageFromPrefs()

        val initialRawConfig = rawConfig.ifBlank {
            val active = if (selectedIndex in configs.indices) configs[selectedIndex] else null
            configTemplateProvider.generateDefaultJson(active?.id ?: "", active?.key ?: "")
        }

        vm.initializeConfigs(
            configs,
            selectedIndex,
            initialRawConfig,
            lang
        )

        // Listen for UI effects emitted by the pure parameterless ViewModel
        lifecycleScope.launch {
            vm.uiEffect.collect { effect ->
                when (effect) {
                    is AndroidUiEffect.SaveSettings -> {
                        withContext(Dispatchers.IO) {
                            vpnPreferencesManager.saveConfigsToPrefs(vm.savedConfigs.value, vm.selectedConfigIndex.value)
                            vpnPreferencesManager.saveSettingsToPrefs(vm.rawConfigJson.value, vm.language.value)
                        }
                    }
                    is AndroidUiEffect.ConnectVpn -> {
                        val isRunning = ProxyService.isVpnRunning.value
                        if (isRunning) {
                            vpnServiceManager.stopVpnService()
                        } else {
                            val vpnIntent = VpnService.prepare(this@MainActivity)
                            if (vpnIntent != null) {
                                vpnPrepareLauncher.launch(vpnIntent)
                            } else {
                                vpnServiceManager.startVpnService()
                            }
                        }
                    }
                    is AndroidUiEffect.CheckAndSaveCertificate -> {
                        lifecycleScope.launch {
                            val caCertFile = vpnCertificateManager.checkAndGenerateCertificate()
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
                    is AndroidUiEffect.UninstallCertificate -> {
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
                }
            }
        }

        // Listen to vm.language flow and recreate Activity if selected language differs from active context locale
        lifecycleScope.launch {
            vm.language.collect { currentLang ->
                if (vpnLanguageManager.isCurrentLocaleDifferent(this@MainActivity, currentLang)) {
                    recreate()
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            val data = this.intent.data
            data?.toString()?.let {
                DeepLinkHandler.setDeepLink(it)
            }

            val homeState by vm.homeState.collectAsState()
            val showInstructionsDialog by vm.showInstructionsDialog.collectAsState()

            App(
                connectivityHandler = koinInject(),
                state = homeState,
                onClick = { event ->
                    when (event) {
                        Event.LoadDefaultConfig -> {
                            val configsState = homeState.savedConfigs
                            val indexState = homeState.selectedConfigIndex
                            val active = if (indexState in configsState.indices) configsState[indexState] else null
                            val defaultContent = configTemplateProvider.generateDefaultJson(active?.id ?: "", active?.key ?: "")
                            vm.onLoadDefaultConfig(defaultContent)
                        }
                        else -> vm.handleEvent(event)
                    }
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
