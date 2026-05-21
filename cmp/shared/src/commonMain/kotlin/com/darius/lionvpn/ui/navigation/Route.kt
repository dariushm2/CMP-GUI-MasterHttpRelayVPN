package com.darius.lionvpn.ui.navigation

import androidx.navigation.NamedNavArgument

sealed class Route(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList(),
) {
    object Home : Route(route = "home")
    object Debug : Route(route = "debug")
}
