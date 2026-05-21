package com.darius.lionvpn.ui.debug

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darius.lionvpn.ui.components.TopBar
import com.darius.lionvpn.ui.model.ToolbarData
import com.darius.lionvpn.ui.navigation.Screen

@Composable
fun DebugScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        topBar = {
            TopBar(navController = navController, toolbar = ToolbarData(title = Screen.Debug.title))
        },
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.statusBars)
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("items go here")
            }
        }
    }
}
