package com.darius.lionvpn

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
import com.darius.lionvpn.connectivity.ConnectivityHandler
import com.darius.lionvpn.ui.debug.DebugScreen
import com.darius.lionvpn.ui.errostate.NetworkLoss
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeScreen
import com.darius.lionvpn.ui.navigation.Route
import com.darius.lionvpn.ui.theme.WalletTheme

@Composable
fun App(
    connectivityHandler: ConnectivityHandler,
    initialScriptId: String = "",
    initialAuthKey: String = "",
    isVpnRunning: Boolean = false,
    onSaveConfig: (String, String) -> Unit = { _, _ -> },
    onClick: (Event) -> Unit = {},
    log: List<String>? = null,
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
                else NavGraph(
                    navController = navController,
                    initialScriptId = initialScriptId,
                    initialAuthKey = initialAuthKey,
                    isVpnRunning = isVpnRunning,
                    onSaveConfig = onSaveConfig,
                    onClick = onClick,
                    log = log,
                )
                // if (isDebugBuild()) DebugButton(navController = navController)
            }
        }
    }
}

@Composable
private fun NavGraph(
    navController: NavHostController,
    initialScriptId: String,
    initialAuthKey: String,
    isVpnRunning: Boolean,
    onSaveConfig: (String, String) -> Unit,
    onClick: (Event) -> Unit,
    log: List<String>?,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
    ) {
        composable(route = Route.Home.route) { 
            HomeScreen(
                navController = navController,
                initialScriptId = initialScriptId,
                initialAuthKey = initialAuthKey,
                isVpnRunning = isVpnRunning,
                onSaveConfig = onSaveConfig,
                onClick = onClick,
                log = log,
            )
        }
        composable(route = Route.Debug.route) { DebugScreen(navController) }
    }
}

