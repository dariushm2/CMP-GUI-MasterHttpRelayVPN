package com.darius.relay_vpn.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Route(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList(),
) {
    object Home : Route(route = "home")
    object Debug : Route(route = "debug")
}
