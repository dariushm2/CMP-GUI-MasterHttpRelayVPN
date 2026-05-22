package com.darius.lionvpn.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedConfig(
    val id: String,
    val key: String,
    val name: String
)
