package com.darius.lionvpn

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CertInstructionsDialog(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🔒 Install HTTPS CA Certificate",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "To successfully intercept and relay HTTPS traffic, you must install the generated CA certificate as a trusted credential on your device.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Please follow these manual steps precisely:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                val steps = listOf(
                    "1. Locate the file: The certificate has been saved to your storage as 'lion_vpn_ca.crt'.",
                    "2. Open security settings: Tap the 'Open Security Settings' button below.",
                    "3. Go to credentials: Find 'More security settings' -> 'Encryption & credentials' (or 'Install from device storage').",
                    "4. Choose CA certificate: Tap 'Install a certificate' -> 'CA certificate'.",
                    "5. Confirm warning: Tap 'Install anyway' on the security warning.",
                    "6. Select the file: Locate and select 'lion_vpn_ca.crt' from your saved folder."
                )

                steps.forEach { step ->
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        try {
                            context.startActivity(Intent(Settings.ACTION_SETTINGS))
                        } catch (ex: Exception) {
                            ProxyService.addLogLine("Error opening Settings: ${ex.message}")
                            Toast.makeText(context, "Could not open system settings automatically.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            ) {
                Text("Open Security Settings", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
