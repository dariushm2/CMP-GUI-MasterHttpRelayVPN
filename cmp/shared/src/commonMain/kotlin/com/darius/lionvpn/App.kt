package com.darius.lionvpn

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darius.lionvpn.connectivity.ConnectivityHandler
import com.darius.lionvpn.ui.debug.DebugScreen
import com.darius.lionvpn.ui.errostate.NetworkLoss
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeScreen
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.navigation.Route
import com.darius.lionvpn.ui.theme.WalletTheme

@Composable
fun App(
    connectivityHandler: ConnectivityHandler,
    state: HomeState,
    onClick: (Event) -> Unit = {},
) {
    val navController = rememberNavController()
    val isConnected by connectivityHandler.isConnected.collectAsState(true)
    val deepLink by DeepLinkHandler.deeplink.collectAsState()

    LaunchedEffect(deepLink) {
        deepLink?.let {
            try {
                navController.navigate(route = it)
            } catch (e: IllegalArgumentException) {
                e.message?.let { message ->
                    debugLog(message)
                }
            }
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        WalletTheme {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                ) {
                    if (!isConnected) NetworkLoss()
                    else NavGraph(
                        navController = navController,
                        state = state,
                        onClick = onClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun NavGraph(
    navController: NavHostController,
    state: HomeState,
    onClick: (Event) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
    ) {
        composable(route = Route.Home.route) { 
            HomeScreen(
                state = state,
                onClick = onClick,
            )
        }
        composable(route = Route.Debug.route) { DebugScreen(navController) }
    }
}
