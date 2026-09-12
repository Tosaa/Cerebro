package redtoss.creativity.cerebro.ui.layouts.lists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.ui.layouts.EmptyState
import redtoss.creativity.cerebro.ui.layouts.LoadingState
import redtoss.creativity.cerebro.ui.layouts.cards.StrategyPreviewCard
import redtoss.creativity.cerebro.ui.screens.Screens
import redtoss.creativity.cerebro.ui.screens.navigateToScreen
import redtoss.creativity.cerebro.ui.theme2.Spacing

@Composable
fun StrategyCardsList(
    title: String,
    strategies: List<Strategy>?,
    navHost: NavHostController,
    emptyMessage: String = "No strategies here yet.",
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.Small),
    ) {
        strategyCardsList(
            title = title,
            strategies = strategies,
            navHost = navHost,
            emptyMessage = emptyMessage,
        )
    }
}

fun LazyListScope.strategyCardsList(
    title: String,
    strategies: List<Strategy>?,
    navHost: NavHostController,
    emptyMessage: String = "No strategies here yet.",
) {
    item(key = "title") {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = Spacing.Small),
        )
    }
    val uniqueStrategies = strategies?.distinct()
    when {
        uniqueStrategies == null -> item(key = "loading") { LoadingState() }
        uniqueStrategies.isEmpty() -> item(key = "empty") { EmptyState(emptyMessage) }
        else -> items(uniqueStrategies, key = { it.hashCode() }) { strategy ->
            StrategyPreviewCard(
                strategy = strategy,
                modifier = Modifier.padding(bottom = Spacing.Small),
            ) { navHost.navigateToScreen(Screens.Strategy(strategy)) }
        }
    }
}
