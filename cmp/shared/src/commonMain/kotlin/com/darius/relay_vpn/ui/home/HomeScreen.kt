package com.darius.relay_vpn.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    initialScriptId: String,
    initialAuthKey: String,
    isVpnRunning: Boolean,
    onSaveConfig: (String, String) -> Unit,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier,
    log: List<String>? = null,
) {
    var scriptId by remember { mutableStateOf(initialScriptId) }
    var authKey by remember { mutableStateOf(initialAuthKey) }
    var isSavedSuccessfully by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Clean tracking of whether the application has been successfully configured
    var isConfigured by remember { 
        mutableStateOf(initialScriptId.isNotEmpty() && initialAuthKey.isNotEmpty()) 
    }

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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                log?.let {
                    Text(
                        text = it.takeLast(5).joinToString("\n"),
                        maxLines = 5,
                        minLines = 5,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                    )
                }

                // Dynamic Status & Control Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
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

                // Configuration Section (Hidden when configured successfully)
                if (!isConfigured) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Relay Configuration Settings",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )

                            OutlinedTextField(
                                value = scriptId,
                                onValueChange = {
                                    scriptId = it
                                    isSavedSuccessfully = false
                                    errorMessage = ""
                                },
                                label = { Text("Apps Script Deployment ID") },
                                placeholder = { Text("Enter your Google Apps Script Deployment ID") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Deployment ID Icon"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = authKey,
                                onValueChange = {
                                    authKey = it
                                    isSavedSuccessfully = false
                                    errorMessage = ""
                                },
                                label = { Text("Relay Auth Key") },
                                placeholder = { Text("Enter your secure Auth Key") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Auth Key Icon"
                                    )
                                },
                                trailingIcon = {
                                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Text(
                                            text = if (passwordVisible) "Hide" else "Show",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    if (scriptId.isBlank()) {
                                        errorMessage = "Deployment ID cannot be blank."
                                    } else if (authKey.isBlank()) {
                                        errorMessage = "Auth Key cannot be blank."
                                    } else {
                                        onSaveConfig(scriptId.trim(), authKey.trim())
                                        isSavedSuccessfully = true
                                        isConfigured = true
                                        errorMessage = ""
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Save Configuration",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                        }
                    }
                } else {
                    // Configuration Active - Mini visual display
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Active Configuration",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                // Mask Deployment ID partly for premium, secure aesthetic
                                val displayId = if (scriptId.length > 10) {
                                    "${scriptId.take(6)}...${scriptId.takeLast(4)}"
                                } else {
                                    "Configured"
                                }
                                Text(
                                    text = "Deployment ID: $displayId",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            // Reveal edit panel button (only when not connected)
                            if (!isVpnRunning) {
                                OutlinedButton(
                                    onClick = { isConfigured = false },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Edit Config",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }

                // Error Feedback Banner
                if (errorMessage.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Certificate Setup Section (Only shown when disconnected)
                if (!isVpnRunning) {
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
                        containerColor = if (isVpnRunning) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    enabled = scriptId.isNotEmpty() && authKey.isNotEmpty() // Require configuration to connect
                ) {
                    Text(
                        text = if (isVpnRunning) "Disconnect & Stop VPN Server" else "Connect & Start VPN Server",
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
