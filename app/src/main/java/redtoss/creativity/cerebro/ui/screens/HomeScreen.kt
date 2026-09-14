package redtoss.creativity.cerebro.ui.screens

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import redtoss.creativity.cerebro.data.Category
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.ui.layouts.LoadingState
import redtoss.creativity.cerebro.ui.layouts.cards.CategoryCard
import redtoss.creativity.cerebro.ui.layouts.cards.StrategyPreviewCard
import redtoss.creativity.cerebro.ui.sampleStrategies
import redtoss.creativity.cerebro.ui.theme.CosyAppTheme
import redtoss.creativity.cerebro.ui.theme.Spacing
import java.time.LocalDate
import kotlin.random.Random

private val CategoryTileMinWidth = 160.dp
private const val CONTENT_FADE_MILLIS = 200

@Suppress("MagicNumber")
@Composable
internal fun HomeScreen(strategies: State<List<Strategy>?>, navHost: NavHostController) {
    val randomSeed = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Random(LocalDate.now().hashCode())
        } else {
            Random(System.currentTimeMillis().floorDiv(1000L).floorDiv(60).floorDiv(60).floorDiv(24))
        }
    }
    val loadedStrategies = strategies.value
    val randomStrategy = remember(loadedStrategies) {
        loadedStrategies?.takeIf { it.isNotEmpty() }?.random(randomSeed)
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = CategoryTileMinWidth),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.Small),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
        verticalArrangement = Arrangement.spacedBy(Spacing.Small),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }, key = "strategy-of-the-day") {
            StrategyOfTheDay(
                randomStrategy = randomStrategy,
                isLoading = loadedStrategies == null,
                navHost = navHost,
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }, key = "categories-header") {
            Text("Categories", style = MaterialTheme.typography.headlineMedium)
        }
        items(Category.entries, key = { it.name }) { category ->
            CategoryCard(
                category = category,
                strategyCount = loadedStrategies?.count { it.category == category },
            ) { navHost.navigateToScreen(Screens.Category(category)) }
        }
    }
}

@Composable
private fun StrategyOfTheDay(randomStrategy: Strategy?, isLoading: Boolean, navHost: NavHostController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.Small),
    ) {
        Text(text = "Strategy of the day", style = MaterialTheme.typography.headlineMedium)
        AnimatedContent(
            targetState = if (isLoading) null else randomStrategy,
            transitionSpec = { fadeIn(tween(CONTENT_FADE_MILLIS)) togetherWith fadeOut(tween(CONTENT_FADE_MILLIS)) },
            label = "Strategy of the day",
        ) { strategy ->
            when {
                strategy != null -> StrategyPreviewCard(strategy = strategy) {
                    navHost.navigateToScreen(Screens.Strategy(strategy))
                }

                isLoading -> LoadingState()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview() = CosyAppTheme {
    HomeScreen(
        strategies = remember { mutableStateOf(sampleStrategies) },
        navHost = rememberNavController(),
    )
}
