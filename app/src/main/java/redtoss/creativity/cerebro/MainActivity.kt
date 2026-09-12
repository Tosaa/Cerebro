package redtoss.creativity.cerebro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import redtoss.creativity.cerebro.data.StrategyProvider
import redtoss.creativity.cerebro.ui.screens.AppUi
import redtoss.creativity.cerebro.ui.theme2.CosyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val assetManager = application.assets
        enableEdgeToEdge()
        setContent {
            CosyAppTheme {
                val strategyProvider = remember { StrategyProvider(assetManager, baseContext) }
                AppUi(strategyProvider)
            }
        }
    }
}
