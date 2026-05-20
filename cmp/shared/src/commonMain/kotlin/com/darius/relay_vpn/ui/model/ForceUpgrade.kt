package com.darius.relay_vpn.ui.model

data class ForceUpgrade(
    val message: String,
    val onUpdateClick: () -> Unit = {},
)
