package redtoss.creativity.cerebro.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.ui.layouts.cards.StrategyCard
import redtoss.creativity.cerebro.ui.sampleStrategy
import redtoss.creativity.cerebro.ui.theme.CosyAppTheme
import redtoss.creativity.cerebro.ui.theme.Spacing

@Composable
internal fun StrategyScreen(strategy: Strategy) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.Small),
    ) {
        StrategyCard(strategy)
    }
}

@PreviewLightDark
@Composable
private fun StrategyScreenPreview() = CosyAppTheme {
    StrategyScreen(sampleStrategy)
}
