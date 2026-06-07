package com.darius.lionvpn.config

import com.darius.lionvpn.ui.model.SavedConfig
import kotlinx.serialization.Serializable

@Serializable
data class LionVpnConf(
    val savedConfigs: List<SavedConfig> = emptyList(),
    val selectedConfigIndex: Int = -1,
    val language: String = "fa",
)
