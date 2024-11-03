package redtoss.creativity.cerebro.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.NavHostController
import redtoss.creativity.cerebro.data.Category
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.ui.layouts.lists.StrategyCardsList

@Composable
internal fun CategoryScreen(category: Category, strategies: State<List<Strategy>>, navHost: NavHostController) {
    val filteredStrategies = strategies.value.filter { it.category == category }
    StrategyCardsList(title = category.title, strategies = filteredStrategies, navHost = navHost)
}
