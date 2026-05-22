package com.darius.lionvpn.ui.home

import com.darius.lionvpn.ui.model.SavedConfig

data class HomeState(
    val isVpnRunning: Boolean = false,
    val log: List<String>? = null,
    val savedConfigs: List<SavedConfig> = emptyList(),
    val selectedConfigIndex: Int = -1
)
