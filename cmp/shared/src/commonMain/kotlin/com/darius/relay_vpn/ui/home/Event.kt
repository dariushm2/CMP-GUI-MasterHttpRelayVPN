package com.darius.relay_vpn.ui.home

sealed interface Event {
    object Connect : Event
    object Certificate : Event
}