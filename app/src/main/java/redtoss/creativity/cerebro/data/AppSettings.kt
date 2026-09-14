package redtoss.creativity.cerebro.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppSettings(context: Context) {
    private val preferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(readThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _colorTheme = MutableStateFlow(readColorTheme())
    val colorTheme: StateFlow<ColorTheme> = _colorTheme.asStateFlow()

    fun setThemeMode(themeMode: ThemeMode) {
        _themeMode.value = themeMode
        preferences.edit { putString(KEY_THEME_MODE, themeMode.name) }
    }

    fun setColorTheme(colorTheme: ColorTheme) {
        _colorTheme.value = colorTheme
        preferences.edit { putString(KEY_COLOR_THEME, colorTheme.name) }
    }

    private fun readThemeMode(): ThemeMode {
        val stored = preferences.getString(KEY_THEME_MODE, null)
        return ThemeMode.entries.firstOrNull { it.name == stored } ?: ThemeMode.System
    }

    private fun readColorTheme(): ColorTheme {
        val stored = preferences.getString(KEY_COLOR_THEME, null)
        return ColorTheme.entries.firstOrNull { it.name == stored } ?: ColorTheme.Cosy
    }

    private companion object {
        const val FILENAME = "cerebro_settings"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_COLOR_THEME = "color_theme"
    }
}
