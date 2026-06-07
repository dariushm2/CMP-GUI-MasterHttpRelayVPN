package com.darius.lionvpn

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.darius.lionvpn.config.ConfigTemplateProvider
import com.darius.lionvpn.config.VpnCertificateManager
import com.darius.lionvpn.config.VpnLanguageManager
import com.darius.lionvpn.config.VpnPreferencesManager
import com.darius.lionvpn.config.VpnServiceManager
import com.darius.lionvpn.model.AndroidUiEffect
import com.darius.lionvpn.ui.CertInstructionsDialog
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.koinInject
import android.content.Intent
import android.provider.Settings
import java.io.File

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

    private var pendingVpnConnectionAction: (() -> Unit)? = null

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            ProxyService.addLogLine(VpnLogger.formatInfo("Notification permission granted."))
        } else {
            ProxyService.addLogLine(VpnLogger.formatInfo("Notification permission denied."))
        }
        pendingVpnConnectionAction?.invoke()
        pendingVpnConnectionAction = null
    }

    override fun attachBaseContext(newBase: Context) {
        val languageManager = VpnLanguageManager(VpnPreferencesManager(newBase))
        super.attachBaseContext(languageManager.applyLocaleToContext(newBase))
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        val lang = vpnPreferencesManager.loadLanguageFromPrefs()
        val locale = java.util.Locale.forLanguageTag(lang.label)
        java.util.Locale.setDefault(locale)
        newConfig.setLocales(android.os.LocaleList(locale))
        newConfig.setLayoutDirection(locale)
        super.onConfigurationChanged(newConfig)
        vpnLanguageManager.applyLocaleToContext(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getKoin().get<ContextFactory>().attach(this)

        initializeVpnConfigs()
        observeUiEffects()
        observeLanguageChanges()

        enableEdgeToEdge()
        setupContent()
    }

    private fun initializeVpnConfigs() {
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
    }

    private fun observeUiEffects() {
        lifecycleScope.launch {
            vm.uiEffect.collect { effect ->
                handleUiEffect(effect)
            }
        }
    }

    private suspend fun handleUiEffect(effect: AndroidUiEffect) {
        when (effect) {
            is AndroidUiEffect.SaveSettings -> {
                withContext(Dispatchers.IO) {
                    vpnPreferencesManager.saveConfigsToPrefs(
                        vm.savedConfigs.value,
                        vm.selectedConfigIndex.value
                    )
                    vpnPreferencesManager.saveSettingsToPrefs(
                        vm.rawConfigJson.value,
                        vm.language.value
                    )
                }
            }
            is AndroidUiEffect.ConnectVpn -> {
                handleConnectVpn()
            }
            is AndroidUiEffect.CheckAndSaveCertificate -> {
                handleCheckAndSaveCertificate()
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
                        Toast.makeText(
                            this@MainActivity,
                            "Could not open system settings automatically.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun handleCheckAndSaveCertificate() {
        lifecycleScope.launch {
            val caCertFile = vpnCertificateManager.checkAndGenerateCertificate()
            resolvedCaCertFile = caCertFile
            ProxyService.addLogLine("Searching for CA certificate at: ${caCertFile.absolutePath}")
            if (!caCertFile.exists()) {
                val errorMsg = "Error: CA certificate was not found at ${caCertFile.absolutePath}. " +
                        "Please connect to the VPN at least once to start the proxy and " +
                        "generate the certificate."
                ProxyService.addLogLine(errorMsg)
                Toast.makeText(
                    this@MainActivity,
                    "Please connect to the VPN at least once to generate the certificate.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                saveCertLauncher.launch("lion_vpn_ca.crt")
            }
        }
    }

    private fun observeLanguageChanges() {
        lifecycleScope.launch {
            vm.language.collect { currentLang ->
                if (vpnLanguageManager.isCurrentLocaleDifferent(this@MainActivity, currentLang)) {
                    recreate()
                }
            }
        }
    }

    private fun setupContent() {
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
                    handleAppEvent(event, homeState)
                }
            )

            if (showInstructionsDialog) {
                CertInstructionsDialog(
                    onDismiss = { vm.setInstructionsDialogVisible(false) }
                )
            }
        }
    }

    private fun handleAppEvent(event: Event, homeState: HomeState) {
        when (event) {
            Event.LoadDefaultConfig -> {
                val configsState = homeState.savedConfigs
                val indexState = homeState.selectedConfigIndex
                val active = if (indexState in configsState.indices) configsState[indexState] else null
                val defaultContent = configTemplateProvider.generateDefaultJson(
                    active?.id ?: "",
                    active?.key ?: ""
                )
                vm.onLoadDefaultConfig(defaultContent)
            }
            else -> vm.handleEvent(event)
        }
    }

    private fun handleConnectVpn() {
        val isRunning = ProxyService.isVpnRunning.value
        if (isRunning) {
            vpnServiceManager.stopVpnService()
        } else {
            val startVpnAction = {
                val vpnIntent = VpnService.prepare(this@MainActivity)
                if (vpnIntent != null) {
                    vpnPrepareLauncher.launch(vpnIntent)
                } else {
                    vpnServiceManager.startVpnService()
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startVpnAction()
                } else {
                    pendingVpnConnectionAction = startVpnAction
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                startVpnAction()
            }
        }
    }
}
