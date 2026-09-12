package redtoss.creativity.cerebro.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import redtoss.creativity.cerebro.data.Category
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.ui.layouts.lists.StrategyCardsList
import redtoss.creativity.cerebro.ui.sampleStrategies
import redtoss.creativity.cerebro.ui.theme2.CosyAppTheme

@Composable
internal fun CategoryScreen(category: Category, strategies: State<List<Strategy>?>, navHost: NavHostController) {
    val filteredStrategies = strategies.value?.filter { it.category == category }
    StrategyCardsList(
        title = category.title,
        strategies = filteredStrategies,
        navHost = navHost,
        emptyMessage = "No ${category.title} strategies yet.",
    )
}

@PreviewLightDark
@Composable
private fun CategoryScreenPreview() = CosyAppTheme {
    CategoryScreen(
        category = Category.Perspective,
        strategies = remember { mutableStateOf(sampleStrategies) },
        navHost = rememberNavController(),
    )
}
