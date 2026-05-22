package com.darius.lionvpn.ui.home

import com.darius.lionvpn.ui.model.SavedConfig

sealed interface Event {
    object Connect : Event
    object Certificate : Event
    data class AddConfig(val config: SavedConfig) : Event
    data class DeleteConfig(val config: SavedConfig) : Event
    data class SelectConfig(val index: Int) : Event
}