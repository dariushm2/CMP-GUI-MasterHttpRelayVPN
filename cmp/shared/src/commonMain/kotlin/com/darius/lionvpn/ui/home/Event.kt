package com.darius.lionvpn.ui.home

import com.darius.lionvpn.ui.model.SavedConfig

sealed interface Event {
    object Connect : Event
    object InstallCertificate : Event
    object UninstallCertificate : Event
    object ClearLogs : Event
    data class AddConfig(val config: SavedConfig) : Event
    data class DeleteConfig(val config: SavedConfig) : Event
    data class SelectConfig(val index: Int) : Event
    data class SaveRawConfig(val json: String) : Event
    object LoadDefaultConfig : Event
}