package com.darius.relay_vpn

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darius.relay_vpn.connectivity.ConnectivityHandler
import com.darius.relay_vpn.ui.components.DebugButton
import com.darius.relay_vpn.ui.debug.DebugScreen
import com.darius.relay_vpn.ui.errostate.NetworkLoss
import com.darius.relay_vpn.ui.home.HomeScreen
import com.darius.relay_vpn.ui.navigation.Route
import com.darius.relay_vpn.ui.theme.WalletTheme

@Composable
fun App(
    connectivityHandler: ConnectivityHandler,
    onClick: () -> Unit = {},
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
                else NavGraph(navController, onClick)
                if (isDebugBuild()) DebugButton(navController = navController)
            }
        }
    }
}

@Composable
private fun NavGraph(
    navController: NavHostController,
    onClick: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
    ) {
        composable(route = Route.Home.route) { HomeScreen(navController, onClick) }
        composable(route = Route.Debug.route) { DebugScreen(navController) }
    }
}
