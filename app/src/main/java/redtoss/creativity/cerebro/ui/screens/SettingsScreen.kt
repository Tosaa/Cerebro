package redtoss.creativity.cerebro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import redtoss.creativity.cerebro.data.ThemeMode
import redtoss.creativity.cerebro.ui.theme2.CosyAppTheme
import redtoss.creativity.cerebro.ui.theme2.Spacing

// Rough ideas for what else could live here:
//
//  - Dynamic colour (Material You) toggle. CosyAppTheme already takes a `dynamicColor`
//    parameter, hardcoded to false at the call site. It discards the warm amber palette
//    in favour of the system wallpaper colours, which is an identity trade-off.
//  - Contrast level: normal / medium / high. theme2/Theme.kt generates mediumContrast
//    and highContrast schemes for both light and dark; all four are declared and never
//    referenced. Mostly plumbing.
//  - A daily "Strategy of the day" reminder notification. Needs POST_NOTIFICATIONS on
//    API 33+, a WorkManager dependency, and a time picker.
//  - Export custom strategies to a JSON file, and import them back. The storage format
//    in StrategyProvider is already JSON, so this is mostly a SAF document picker.
//  - Reset custom strategies: delete the private custom_strategies.json. Needs a
//    confirmation dialog; it is destructive and unrecoverable.
//  - Which screen the app opens on: Home or Library.

@Composable
fun SettingsScreen(themeMode: ThemeMode, onThemeModeSelected: (ThemeMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.Large),
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        ThemeSetting(themeMode = themeMode, onThemeModeSelected = onThemeModeSelected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSetting(themeMode: ThemeMode, onThemeModeSelected: (ThemeMode) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "System follows your device's light or dark setting.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ThemeMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = mode == themeMode,
                    onClick = { onThemeModeSelected(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = ThemeMode.entries.size),
                ) {
                    Text(mode.label)
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() = CosyAppTheme {
    val themeMode = remember { mutableStateOf(ThemeMode.System) }
    SettingsScreen(themeMode = themeMode.value) { themeMode.value = it }
}
