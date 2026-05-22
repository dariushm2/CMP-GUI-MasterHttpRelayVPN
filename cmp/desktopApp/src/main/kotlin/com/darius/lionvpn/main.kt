package com.darius.lionvpn

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.res.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Suppress("DEPRECATION")
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
        icon = painterResource("logo.png"),
    ) {
        currentWindowHolder.window = this.window
        val viewModel: AppViewModel = koinViewModel<AppViewModel>()
        val homeState by viewModel.homeState.collectAsState()

        App(
            connectivityHandler = koinInject(),
            state = homeState,
            onClick = { event ->
                viewModel.handleEvent(event)
            }
        )
    }
}
