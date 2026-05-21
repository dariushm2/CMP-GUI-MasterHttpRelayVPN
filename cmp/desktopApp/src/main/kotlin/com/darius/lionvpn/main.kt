package com.darius.lionvpn

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


fun main() = application {
    initKoin()

    // Bulletproof fail-safe JVM Shutdown Hook to clean up spawned background processes
    Runtime.getRuntime().addShutdownHook(Thread {
        println("[Shutdown Hook] Force stopping active VPN daemon...")
        ProcessRunner.stop()
    })

    val (initialScriptId, initialAuthKey) = loadSavedConfig()

    Window(
        onCloseRequest = {
            println("[Window] Closing application, stopping active VPN daemon...")
            ProcessRunner.stop()
            exitApplication()
        },
        title = "Lion VPN",
    ) {
        currentWindowHolder.window = this.window
        val viewModel: AppViewModel = koinViewModel<AppViewModel>()
        val isVpnRunning by viewModel.isVpnRunning.collectAsState()
        val vpnLogs by viewModel.vpnLogs.collectAsState()
        App(
            connectivityHandler = koinInject(),
            initialScriptId = initialScriptId,
            initialAuthKey = initialAuthKey,
            isVpnRunning = isVpnRunning,
            onSaveConfig = { id, key ->
                saveConfigLocally(id, key)
            },
            onClick = { event ->
                viewModel.handleEvent(event)
            },
            log = if (isDebugBuild()) vpnLogs else null,
        )
    }
}
