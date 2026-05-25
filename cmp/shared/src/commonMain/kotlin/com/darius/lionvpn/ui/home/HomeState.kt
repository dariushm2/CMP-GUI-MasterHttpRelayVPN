package com.darius.lionvpn.ui.home

import com.darius.lionvpn.ui.model.SavedConfig

data class HomeState(
    val isVpnRunning: Boolean = false,
    val log: List<String> = emptyList(),
    val savedConfigs: List<SavedConfig> = emptyList(),
    val selectedConfigIndex: Int = -1,
    val rawConfigJson: String = "",
    val configResetTrigger: Int = 0
)