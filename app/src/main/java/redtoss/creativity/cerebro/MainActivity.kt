package redtoss.creativity.cerebro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import redtoss.creativity.cerebro.data.AppSettings
import redtoss.creativity.cerebro.data.StrategyProvider
import redtoss.creativity.cerebro.data.ThemeMode
import redtoss.creativity.cerebro.ui.screens.AppUi
import redtoss.creativity.cerebro.ui.theme2.CosyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val assetManager = application.assets
        enableEdgeToEdge()
        setContent {
            val appSettings = remember { AppSettings(applicationContext) }
            val themeMode by appSettings.themeMode.collectAsStateWithLifecycle()
            val colorTheme by appSettings.colorTheme.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                ThemeMode.System -> isSystemInDarkTheme()
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }

            // The system bar icons follow the system's night mode, not the app's, so
            // choosing Light while the device is in dark mode would leave light icons
            // on a light bar.
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            CosyAppTheme(darkTheme = darkTheme, colorTheme = colorTheme) {
                val strategyProvider = remember { StrategyProvider(assetManager, baseContext) }
                AppUi(strategyProvider, appSettings)
            }
        }
    }
}
