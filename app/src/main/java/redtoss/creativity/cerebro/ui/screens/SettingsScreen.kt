package redtoss.creativity.cerebro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import redtoss.creativity.cerebro.ui.layouts.Spacing
import redtoss.creativity.cerebro.ui.theme2.CosyAppTheme

/**
 * Placeholder. Nothing here is implemented yet — the screen exists so the overflow menu
 * item leads somewhere honest instead of silently doing nothing.
 *
 * Rough ideas for what could live here, recorded so the shape of the screen is not
 * re-invented later. None of this is built, and some of it implies work well outside
 * the UI layer:
 *
 *  - Theme mode override: system / light / dark. Today CosyAppTheme reads
 *    isSystemInDarkTheme() directly, so this needs the choice hoisted above the theme
 *    and persisted (DataStore) before the toggle can mean anything.
 *  - Dynamic colour (Material You) toggle. CosyAppTheme already takes a `dynamicColor`
 *    parameter, hardcoded to false at the call site — this would just expose it. Note
 *    it discards the warm amber palette in favour of the system wallpaper colours,
 *    which is a real identity trade-off, not a free win.
 *  - Contrast level: normal / medium / high. theme2/Theme.kt already generates
 *    mediumContrast and highContrast schemes for both light and dark; all four are
 *    currently declared and never referenced. Wiring this is mostly plumbing.
 *  - A daily "Strategy of the day" reminder notification. Needs POST_NOTIFICATIONS on
 *    API 33+, a WorkManager dependency, and a time picker.
 *  - Export custom strategies to a JSON file, and import them back. The storage format
 *    in StrategyProvider is already JSON, so this is mostly a SAF document picker.
 *  - Reset custom strategies — delete the private custom_strategies.json. Needs a
 *    confirmation dialog; it is destructive and unrecoverable.
 *  - Which screen the app opens on: Home or Library.
 *
 * Open question before any of this: settings need somewhere to live. The app currently
 * has no preferences storage at all, so the first item built also decides that.
 */
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.Small),
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Nothing to configure yet.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() = CosyAppTheme { SettingsScreen() }
