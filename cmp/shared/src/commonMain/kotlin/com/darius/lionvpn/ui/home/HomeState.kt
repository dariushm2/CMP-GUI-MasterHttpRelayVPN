package com.darius.lionvpn.ui.home

import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.model.SavedConfig

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

data class HomeState(
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val log: List<String> = emptyList(),
    val savedConfigs: List<SavedConfig> = emptyList(),
    val selectedConfigIndex: Int = -1,
    val rawConfigJson: String = "",
    val configResetTrigger: Int = 0,
    val language: Lang = Lang.FA,
) {
    val isVpnRunning: Boolean
        get() = connectionState != ConnectionState.DISCONNECTED
}