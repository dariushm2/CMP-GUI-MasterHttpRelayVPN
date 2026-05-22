package com.darius.lionvpn.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.darius.lionvpn.ui.model.SavedConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Determine the active configuration
    val activeConfig = if (state.selectedConfigIndex in state.savedConfigs.indices) {
        state.savedConfigs[state.selectedConfigIndex]
    } else {
        null
    }

    val scriptId = activeConfig?.id ?: ""
    val authKey = activeConfig?.key ?: ""

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.statusBars)
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                state.log?.let {
                    LogTerminal(
                        log = it,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Dynamic Status & Control Section
                VpnStatusCard(
                    isVpnRunning = state.isVpnRunning,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Premium Multi-config Management Panel (Default)
                MultiConfigPanel(
                    savedConfigs = state.savedConfigs,
                    selectedConfigIndex = state.selectedConfigIndex,
                    isVpnRunning = state.isVpnRunning,
                    onClick = onClick,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Certificate Setup Section (Only shown when disconnected)
                if (!state.isVpnRunning) {
                    Button(
                        onClick = { onClick(Event.Certificate) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(
                            text = "Install HTTPS Certificate (Requires privileges)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Main Connect / Disconnect button (toggled dynamically based on running state)
                Button(
                    onClick = { onClick(Event.Connect) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isVpnRunning) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    enabled = scriptId.isNotEmpty() && authKey.isNotEmpty() // Require configuration to connect
                ) {
                    Text(
                        text = if (state.isVpnRunning) "Disconnect & Stop VPN Server" else "Connect & Start VPN Server",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }
    }
}
