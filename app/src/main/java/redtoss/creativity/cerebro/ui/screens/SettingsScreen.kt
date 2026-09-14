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
import redtoss.creativity.cerebro.data.ColorTheme
import redtoss.creativity.cerebro.data.LabelledChoice
import redtoss.creativity.cerebro.data.ThemeMode
import redtoss.creativity.cerebro.ui.theme.CosyAppTheme
import redtoss.creativity.cerebro.ui.theme.Spacing

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    colorTheme: ColorTheme,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onColorThemeSelected: (ColorTheme) -> Unit,
) {
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
        ChoiceSetting(
            title = "Appearance",
            description = "System follows your device's light or dark setting.",
            options = ThemeMode.entries,
            selected = themeMode,
            onSelected = onThemeModeSelected,
        )
        ChoiceSetting(
            title = "Colour theme",
            description = "Cosy is warm amber, Forest is green, Ocean is blue.",
            options = ColorTheme.entries,
            selected = colorTheme,
            onSelected = onColorThemeSelected,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : LabelledChoice> ChoiceSetting(
    title: String,
    description: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = option == selected,
                    onClick = { onSelected(option) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                ) {
                    Text(option.label)
                }
            }
        }
    }
}

@Composable
private fun SettingsScreenPreviewBody(colorTheme: ColorTheme) = CosyAppTheme(colorTheme = colorTheme) {
    val themeMode = remember { mutableStateOf(ThemeMode.System) }
    val selectedColorTheme = remember { mutableStateOf(colorTheme) }
    SettingsScreen(
        themeMode = themeMode.value,
        colorTheme = selectedColorTheme.value,
        onThemeModeSelected = { themeMode.value = it },
        onColorThemeSelected = { selectedColorTheme.value = it },
    )
}

@PreviewLightDark
@Composable
private fun SettingsScreenCosyPreview() = SettingsScreenPreviewBody(ColorTheme.Cosy)

@PreviewLightDark
@Composable
private fun SettingsScreenForestPreview() = SettingsScreenPreviewBody(ColorTheme.Forest)

@PreviewLightDark
@Composable
private fun SettingsScreenOceanPreview() = SettingsScreenPreviewBody(ColorTheme.Ocean)
