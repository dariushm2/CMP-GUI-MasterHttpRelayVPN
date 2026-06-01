package com.darius.lionvpn.ui.home.scripts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.surfaceContainerHigh
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.onSecondary
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.outlineVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedDefault
import com.darius.lionvpn.ui.theme.error
import com.darius.lionvpn.ui.theme.bodyMd
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.titleSm
import com.darius.lionvpn.ui.theme.labelCaps
import com.darius.lionvpn.ui.theme.surfaceContainerLowest
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.new_script_profile
import lion_vpn.shared.generated.resources.close_add_profile_icon_desc
import lion_vpn.shared.generated.resources.error_profile_label_blank
import lion_vpn.shared.generated.resources.error_deployment_id_blank
import lion_vpn.shared.generated.resources.error_auth_key_blank
import lion_vpn.shared.generated.resources.profile_label
import lion_vpn.shared.generated.resources.profile_label_placeholder
import lion_vpn.shared.generated.resources.deployment_id_label
import lion_vpn.shared.generated.resources.deployment_id_placeholder
import lion_vpn.shared.generated.resources.deployment_id_icon_desc
import lion_vpn.shared.generated.resources.relay_auth_key_label
import lion_vpn.shared.generated.resources.relay_auth_key_placeholder
import lion_vpn.shared.generated.resources.auth_key_icon_desc
import lion_vpn.shared.generated.resources.hide
import lion_vpn.shared.generated.resources.show
import lion_vpn.shared.generated.resources.save_profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun customTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = onSurface,
    unfocusedTextColor = onSurface,
    disabledTextColor = onSurfaceVariant.copy(alpha = 0.5f),
    focusedContainerColor = surfaceContainerLowest,
    unfocusedContainerColor = surfaceContainerLowest,
    focusedBorderColor = primary,
    unfocusedBorderColor = outlineVariant.copy(alpha = 0.6f),
    focusedPlaceholderColor = onSurfaceVariant.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = onSurfaceVariant.copy(alpha = 0.5f),
    focusedLabelColor = primary,
    unfocusedLabelColor = onSurfaceVariant
)

@Composable
fun AddScriptDialog(
    isVpnRunning: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val errorProfileLabelBlank = stringResource(Res.string.error_profile_label_blank)
    val errorDeploymentIdBlank = stringResource(Res.string.error_deployment_id_blank)
    val errorAuthKeyBlank = stringResource(Res.string.error_auth_key_blank)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = modifier
                .width(480.dp)
                .padding(16.dp),
            shape = roundedLg,
            colors = CardDefaults.cardColors(containerColor = surfaceContainerHigh),
            border = borderStrokeGlass()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AddScriptHeader(onDismiss = onDismiss)
                Divider(color = outlineVariant)

                ProfileLabelField(
                    value = name,
                    isVpnRunning = isVpnRunning,
                    onValueChange = { name = it; formError = "" },
                    focusManager = focusManager
                )

                DeploymentIdField(
                    value = id,
                    isVpnRunning = isVpnRunning,
                    onValueChange = { id = it; formError = "" },
                    focusManager = focusManager
                )

                AuthKeyField(
                    value = key,
                    isVpnRunning = isVpnRunning,
                    onValueChange = { key = it; formError = "" },
                    focusManager = focusManager
                )

                if (formError.isNotEmpty()) {
                    Text(
                        text = formError,
                        color = error,
                        style = bodySm,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                SaveButton(
                    isVpnRunning = isVpnRunning,
                    onClick = {
                        if (name.isBlank()) {
                            formError = errorProfileLabelBlank
                        } else if (id.isBlank()) {
                            formError = errorDeploymentIdBlank
                        } else if (key.isBlank()) {
                            formError = errorAuthKeyBlank
                        } else {
                            onSave(name.trim(), id.trim(), key.trim())
                            name = ""
                            id = ""
                            key = ""
                            formError = ""
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AddScriptHeader(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.new_script_profile),
            style = titleSm.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = secondary
            )
        )
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.close_add_profile_icon_desc),
                tint = onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileLabelField(
    value: String,
    isVpnRunning: Boolean,
    onValueChange: (String) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(Res.string.profile_label),
            style = labelCaps.copy(color = onSurfaceVariant)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = bodyMd.copy(color = onSurface),
            placeholder = {
                Text(
                    text = stringResource(Res.string.profile_label_placeholder),
                    style = bodyMd,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = roundedDefault,
            enabled = !isVpnRunning,
            colors = customTextFieldColors(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeploymentIdField(
    value: String,
    isVpnRunning: Boolean,
    onValueChange: (String) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(Res.string.deployment_id_label),
            style = labelCaps.copy(color = onSurfaceVariant)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = bodyMd.copy(color = onSurface),
            placeholder = {
                Text(
                    text = stringResource(Res.string.deployment_id_placeholder),
                    style = bodyMd,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(Res.string.deployment_id_icon_desc),
                    tint = onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = roundedDefault,
            enabled = !isVpnRunning,
            colors = customTextFieldColors(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthKeyField(
    value: String,
    isVpnRunning: Boolean,
    onValueChange: (String) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(Res.string.relay_auth_key_label),
            style = labelCaps.copy(color = onSurfaceVariant)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = bodyMd.copy(color = onSurface),
            placeholder = {
                Text(
                    text = stringResource(Res.string.relay_auth_key_placeholder),
                    style = bodyMd,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(Res.string.auth_key_icon_desc),
                    tint = onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        text = if (passwordVisible) stringResource(Res.string.hide) else stringResource(Res.string.show),
                        style = bodySm.copy(fontWeight = FontWeight.Bold, color = primary)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = roundedDefault,
            enabled = !isVpnRunning,
            colors = customTextFieldColors(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
    }
}

@Composable
private fun SaveButton(
    isVpnRunning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = secondary,
            contentColor = onSecondary
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = roundedDefault,
        enabled = !isVpnRunning
    ) {
        Text(
            text = stringResource(Res.string.save_profile),
            style = titleSm.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold)
        )
    }
}
