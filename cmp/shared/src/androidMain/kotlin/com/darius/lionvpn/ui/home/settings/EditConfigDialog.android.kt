package com.darius.lionvpn.ui.home.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.surfaceContainerLowest

@Composable
actual fun EditConfigDialog(
    state: HomeState,
    onClick: (Event) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .safeDrawingPadding(),
            shape = roundedLg,
            color = surfaceContainerLowest,
            border = borderStrokeGlass()
        ) {
            ConfigDialogContent(
                state = state,
                onClick = onClick,
                onDismiss = onDismiss
            )
        }
    }
}
