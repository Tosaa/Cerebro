package redtoss.creativity.cerebro.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import redtoss.creativity.cerebro.R
import redtoss.creativity.cerebro.ui.theme2.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navHost: NavHostController) {
    val currentBackStackEntry by navHost.currentBackStackEntryAsState()
    val canNavigateBack = currentBackStackEntry != null && navHost.previousBackStackEntry != null

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton({ navHost.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                }
            }
        },
        actions = {
            val isMenuOpen = remember { mutableStateOf(false) }
            DropdownMenu(
                expanded = isMenuOpen.value,
                onDismissRequest = { isMenuOpen.value = false },
                shape = RoundedCornerShape(Spacing.Small),
                modifier = Modifier.padding(Spacing.Tiny),
            ) {
                MenuItem("About", Icons.Default.Info, isMenuOpen) {
                    navHost.navigateToScreen(Screens.About)
                }
                MenuItem("Library", Icons.AutoMirrored.Filled.List, isMenuOpen) {
                    navHost.navigateToScreen(Screens.StrategyLibrary)
                }
                MenuItem("New Strategy", Icons.Default.Add, isMenuOpen) {
                    navHost.navigateToScreen(Screens.NewStrategy)
                }
                MenuItem("Settings", Icons.Default.Settings, isMenuOpen) {
                    navHost.navigateToScreen(Screens.Settings)
                }
                MenuItem("Unlock all", Icons.Default.Lock, isMenuOpen) {
                    navHost.navigateToScreen(Screens.UnlockAll)
                }
            }
            IconButton({ isMenuOpen.value = !isMenuOpen.value }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }
        },
    )
}

@Composable
private fun MenuItem(
    label: String,
    icon: ImageVector,
    isMenuOpen: MutableState<Boolean>,
    onClicked: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        onClick = {
            onClicked()
            isMenuOpen.value = false
        },
    )
}
