package com.darius.lionvpn.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AddScriptForm(
    isVpnRunning: Boolean,
    onSave: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; formError = "" },
            label = { Text("Profile Name / Label") },
            placeholder = { Text("e.g. Primary, Backup, Script 3") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = !isVpnRunning
        )

        OutlinedTextField(
            value = id,
            onValueChange = { id = it; formError = "" },
            label = { Text("Apps Script Deployment ID") },
            placeholder = { Text("Enter Google Apps Script Deployment ID") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Deployment ID Icon"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = !isVpnRunning
        )

        OutlinedTextField(
            value = key,
            onValueChange = { key = it; formError = "" },
            label = { Text("Relay Auth Key") },
            placeholder = { Text("Enter secure Auth Key") },
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
            shape = RoundedCornerShape(12.dp),
            enabled = !isVpnRunning
        )

        if (formError.isNotEmpty()) {
            Text(
                text = formError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Button(
            onClick = {
                if (name.isBlank()) {
                    formError = "Profile label cannot be blank."
                } else if (id.isBlank()) {
                    formError = "Deployment ID cannot be blank."
                } else if (key.isBlank()) {
                    formError = "Auth Key cannot be blank."
                } else {
                    onSave(name.trim(), id.trim(), key.trim())
                    name = ""
                    id = ""
                    key = ""
                    formError = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(10.dp),
            enabled = !isVpnRunning
        ) {
            Text(
                text = "Save Profile",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
