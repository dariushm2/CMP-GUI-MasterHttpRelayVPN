package com.darius.lionvpn.config

import kotlinx.serialization.Serializable
import com.darius.lionvpn.ui.model.SavedConfig

@Serializable
data class LionVpnConf(
    val savedConfigs: List<SavedConfig> = emptyList(),
    val selectedConfigIndex: Int = -1,
    val language: String = "fa"
)
