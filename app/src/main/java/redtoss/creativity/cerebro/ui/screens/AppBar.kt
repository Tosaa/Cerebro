package redtoss.creativity.cerebro.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import redtoss.creativity.cerebro.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navHost: NavHostController) {
    TopAppBar(title = {
        Column(Modifier.clickable {
            navHost.popBackStack(Screens.Main.route, false)
        }) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            // Not sure whether second line looks better
            // Text(stringResource(R.string.app_motto), style = MaterialTheme.typography.titleSmall, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.secondary)
        }
    }, actions = {
        val isMenuOpen = remember { mutableStateOf(false) }
        DropdownMenu(
            isMenuOpen.value,
            { isMenuOpen.value = false },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(4.dp),
        ) {
            DropdownMenuItem({ Text("About", Modifier.padding(8.dp)) }, {
                navHost.navigateToScreen(Screens.About)
                isMenuOpen.value = false
            })
            DropdownMenuItem({ Text("Library", Modifier.padding(8.dp)) }, {
                navHost.navigateToScreen(Screens.StrategyLibrary)
                isMenuOpen.value = false
            })
            DropdownMenuItem({ Text("Settings", Modifier.padding(8.dp)) }, {
                isMenuOpen.value = false
            })
            DropdownMenuItem({ Text("Unlock all", Modifier.padding(8.dp)) }, {
                isMenuOpen.value = false
            })
            DropdownMenuItem({ Text("New Strategy", Modifier.padding(8.dp)) }, {
                navHost.navigateToScreen(Screens.NewStrategy)
                isMenuOpen.value = false
            })
        }
        IconButton({ isMenuOpen.value = !isMenuOpen.value }) {
            Icon(Icons.Default.Menu, "Menu icon")
        }
    })
}
