package com.darius.lionvpn

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.darius.lionvpn.ui.home.HomeState

fun main() = application {
    initKoin()

    // Bulletproof fail-safe JVM Shutdown Hook to clean up spawned background processes
    Runtime.getRuntime().addShutdownHook(Thread {
        println("[Shutdown Hook] Force stopping active VPN daemon...")
        ProcessRunner.stop()
    })

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
        val savedConfigs by viewModel.savedConfigs.collectAsState()
        val selectedConfigIndex by viewModel.selectedConfigIndex.collectAsState()

        val homeState = remember(isVpnRunning, vpnLogs, savedConfigs, selectedConfigIndex) {
            HomeState(
                isVpnRunning = isVpnRunning,
                log = if (isDebugBuild()) vpnLogs else null,
                savedConfigs = savedConfigs,
                selectedConfigIndex = selectedConfigIndex
            )
        }

        App(
            connectivityHandler = koinInject(),
            state = homeState,
            onClick = { event ->
                viewModel.handleEvent(event)
            }
        )
    }
}
