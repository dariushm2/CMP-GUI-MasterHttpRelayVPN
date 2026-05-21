package com.darius.lionvpn.ui.home

sealed interface Event {
    object Connect : Event
    object Certificate : Event
}