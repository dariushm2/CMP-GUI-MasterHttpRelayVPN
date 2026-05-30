package com.darius.lionvpn.ui.home

import com.darius.lionvpn.getCurrentTimeMillis
import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.model.SavedConfig

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

enum class CertOperationType {
    INSTALL,
    UNINSTALL
}

data class CertOperationResult(
    val type: CertOperationType,
    val isSuccess: Boolean,
    val timestamp: Long = getCurrentTimeMillis()
)

data class HomeState(
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val log: List<String> = emptyList(),
    val savedConfigs: List<SavedConfig> = emptyList(),
    val selectedConfigIndex: Int = -1,
    val rawConfigJson: String = "",
    val configResetTrigger: Int = 0,
    val language: Lang = Lang.FA,
    val certOperationResult: CertOperationResult? = null,
    val isCertTrusted: Boolean = false,
    val isAndroid: Boolean = false,
    val isCertBusy: Boolean = false,
) {
    val isVpnRunning: Boolean
        get() = connectionState != ConnectionState.DISCONNECTED
}