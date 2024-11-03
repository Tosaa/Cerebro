package redtoss.creativity.cerebro.ui.layouts.lists

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.ui.layouts.cards.StrategyPreviewCard
import redtoss.creativity.cerebro.ui.screens.Screens
import redtoss.creativity.cerebro.ui.screens.navigateToScreen

@Composable
fun StrategyCardsList(title: String, strategies: List<Strategy>, navHost: NavHostController) {
    LazyColumn(Modifier.padding(horizontal = 8.dp)) {
        strategyCardsList(title = title, strategies = strategies, navHost = navHost)
    }
}

fun LazyListScope.strategyCardsList(title: String, strategies: List<Strategy>, navHost: NavHostController) {
    item { Text(text = title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 8.dp)) }
    strategies.forEach { strategy ->
        item {
            StrategyPreviewCard(
                strategy = strategy, modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
            ) { navHost.navigateToScreen(Screens.Strategy(strategy)) }
        }
    }
}
