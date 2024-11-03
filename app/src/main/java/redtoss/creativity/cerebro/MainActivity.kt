package redtoss.creativity.cerebro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import redtoss.creativity.cerebro.data.StrategyProvider
import redtoss.creativity.cerebro.ui.screens.AppUi
import redtoss.creativity.cerebro.ui.theme.CerebroTheme
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
