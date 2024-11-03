package redtoss.creativity.cerebro.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import redtoss.creativity.cerebro.data.StrategyProvider
import redtoss.creativity.cerebro.ui.layouts.lists.StrategyCardsList


@Composable
fun LibraryScreen(strategyProvider: StrategyProvider, navHost: NavHostController) {
    val strategies = strategyProvider.resolvedStrategies.collectAsStateWithLifecycle(emptyList())
    StrategyCardsList(title = "Library", strategies = strategies.value.sortedBy { it.title }, navHost = navHost)
}
