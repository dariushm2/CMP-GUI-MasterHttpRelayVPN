package com.darius.lionvpn.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun VpnStatusCard(
    isVpnRunning: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isVpnRunning) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Pulsating/Active status indicator dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (isVpnRunning) Color(0xFF2E7D32) else Color(0xFF757575),
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isVpnRunning) "VPN SERVER RUNNING" else "VPN DISCONNECTED",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isVpnRunning) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            if (isVpnRunning) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Traffic is securely relayed through Google Apps Script proxy.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
