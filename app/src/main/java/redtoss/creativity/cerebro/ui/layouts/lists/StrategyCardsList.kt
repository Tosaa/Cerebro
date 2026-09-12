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
import redtoss.creativity.cerebro.ui.layouts.Spacing
import redtoss.creativity.cerebro.ui.layouts.cards.StrategyPreviewCard
import redtoss.creativity.cerebro.ui.screens.Screens
import redtoss.creativity.cerebro.ui.screens.navigateToScreen

/**
 * The shared list body behind both the Category and Library screens.
 *
 * [strategies] is null while the strategy list is still loading, which is why it is not
 * simply an empty list: the two states need different UI.
 */
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
    // distinct() before keying: a custom strategy can be typed to match a bundled one
    // exactly, and two items sharing a key is a hard error in a lazy list. Showing the
    // same strategy twice would be wrong anyway.
    val uniqueStrategies = strategies?.distinct()
    when {
        uniqueStrategies == null -> item(key = "loading") { LoadingState() }
        uniqueStrategies.isEmpty() -> item(key = "empty") { EmptyState(emptyMessage) }
        // Keyed by hashCode, the same identity the navigation routes already use
        // (see Screens.Strategy). Two strategies that collide here would already open
        // each other's detail screen, so this introduces no new identity assumption.
        else -> items(uniqueStrategies, key = { it.hashCode() }) { strategy ->
            StrategyPreviewCard(
                strategy = strategy,
                modifier = Modifier.padding(bottom = Spacing.Small),
            ) { navHost.navigateToScreen(Screens.Strategy(strategy)) }
        }
    }
}
