package com.darius.relay_vpn.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    val route: String
    val title: String

//    @Serializable
//    data object Home : Screen {
//        override val route: String = Route.Home.route
//        override val title: String = "Home"
//    }

    @Serializable
    data object Debug : Screen {
        override val route: String = Route.Debug.route
        override val title: String = "Debug"
    }
}
