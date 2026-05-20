package com.darius.relay_vpn

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "HTTP Master Relay VPN",
    ) {
        currentWindowHolder.window = this.window
        val viewModel: AppViewModel = koinViewModel<AppViewModel>()
        App(
            connectivityHandler = koinInject(),
            onClick = {
                viewModel.runMain()
            }
        )
    }
}
