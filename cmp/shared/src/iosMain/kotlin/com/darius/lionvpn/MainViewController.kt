package com.darius.lionvpn

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.compose.koinInject

fun MainViewController() = ComposeUIViewController {
    // koin in being initiated from iosApp
    App(connectivityHandler = koinInject())
}
