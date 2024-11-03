package redtoss.creativity.cerebro.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.time.LocalDate
import kotlin.random.Random
import redtoss.creativity.cerebro.data.Category
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.ui.layouts.cards.CategoryCard
import redtoss.creativity.cerebro.ui.layouts.cards.StrategyPreviewCard

@Composable
internal fun HomeScreen(strategies: State<List<Strategy>>, navHost: NavHostController) {
    val randomSeed = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Random(LocalDate.now().hashCode())
        } else {
            Random(System.currentTimeMillis().floorDiv(1000L).floorDiv(60).floorDiv(60).floorDiv(24))
        }
    }
    val randomStrategy = remember(strategies.value) { strategies.value.takeIf { it.isNotEmpty() }?.random(randomSeed) }
    LazyColumn(Modifier.padding(horizontal = 8.dp)) {
        /*item {
            StrategyOfTheDay(randomStrategy, navHost)
        }*/
        item {
            Column {
                Text("Categories", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
            }
        }
        Category.entries.chunked(2).map {
            val first = it.firstOrNull()
            val second = it.getOrNull(1)
            item {
                Row(
                    Modifier
                        .padding(bottom = 8.dp, start = 4.dp, end = 4.dp)
                        .fillMaxWidth()
                ) {
                    first?.let { category ->
                        CategoryCard(
                            category, modifier = Modifier
                                .weight(1f)
                        ) { navHost.navigateToScreen(Screens.Category(category)) }
                    }
                    second?.let { category ->
                        Spacer(Modifier.width(8.dp))
                        CategoryCard(
                            category, modifier = Modifier
                                .weight(1f)
                        ) { navHost.navigateToScreen(Screens.Category(category)) }
                    }
                }
            }
        }

    }
}

@Composable
private fun StrategyOfTheDay(randomStrategy: Strategy?, navHost: NavHostController) {
    Column {
        Text("Strategy of the day:", Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        randomStrategy?.let {
            StrategyPreviewCard(it, Modifier.padding(start = 8.dp, end = 8.dp)) { navHost.navigateToScreen(Screens.Strategy(it)) }
        }
        Spacer(Modifier.height(16.dp))
    }
}