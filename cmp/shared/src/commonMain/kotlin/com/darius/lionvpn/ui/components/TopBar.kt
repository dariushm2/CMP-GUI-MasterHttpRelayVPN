package com.darius.lionvpn.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darius.lionvpn.ui.model.ToolbarData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
    toolbar: ToolbarData?,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            toolbar?.title?.let { Text(it) }
        },
        actions = {
            Row(
                modifier = Modifier
                    .padding(all = 16.dp)
            ) {

                toolbar?.actions?.forEach { actionButton ->
                    IconButton(onClick = {
                        actionButton.action()
                        actionButton.destinationRoute?.let {
                            navController.navigate(it)
                        }
                    }) {
                        Icon(actionButton.icon, contentDescription = actionButton.icon.name)
                    }
                }
            }
        },
        navigationIcon = {
            if (toolbar?.showHomeAsUp == true) {
                BackButton(navController)
            }
        },
        modifier = modifier
            .shadow(elevation = 4.dp)
    )
}
