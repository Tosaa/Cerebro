package redtoss.creativity.cerebro.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import redtoss.creativity.cerebro.data.StrategyProvider

@Composable
fun AppUi(strategyProvider: StrategyProvider) {
    val strategies = strategyProvider.resolvedStrategies.collectAsStateWithLifecycle(emptyList())

    val navHost = rememberNavController()
    Scaffold(topBar = { AppBar(navHost) }) { paddingValues ->
        NavHost(
            navController = navHost, startDestination = Screens.Main.name, modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            composable(Screens.Main.name) {
                HomeScreen(strategies, navHost)
            }

            composable(route = Screens.StrategyLibrary.name, arguments = Screens.StrategyLibrary.arguments, enterTransition = { slideInVertically() }) {
                LibraryScreen(strategyProvider, navHost)
            }

            composable(
                route = Screens.Category.name,
                arguments = Screens.Category.arguments,
                popExitTransition = { slideOutHorizontally { -1 * it } },
                enterTransition = { slideInHorizontally() { it } }) {
                Screens.Category.categoryArgument(it)?.let { category ->
                    CategoryScreen(category = category, strategies = strategies, navHost = navHost)
                } ?: navHost.popBackStack()
            }

            composable(
                route = Screens.Strategy.name,
                arguments = Screens.Strategy.arguments,
                popExitTransition = { slideOutHorizontally { -1 * it } },
                enterTransition = { slideInHorizontally() { it } }) { navBackStackEntry ->
                Screens.Strategy.strategyArgument(navBackStackEntry)?.let { strategyHashCode ->
                    strategies.value.firstOrNull { it.hashCode() == strategyHashCode }?.let {
                        StrategyScreen(it)
                    }
                } ?: navHost.popBackStack()
            }

            composable(
                route = Screens.About.name,
                arguments = Screens.About.arguments,
                popExitTransition = { slideOutVertically() { -1 * it } },
                enterTransition = { slideInVertically() }) {
                AboutScreen()
            }

            composable(route = Screens.NewStrategy.name, arguments = Screens.NewStrategy.arguments) {
                StrategyEditorScreen { strategy ->
                    with(strategyProvider.addCustomStrategy(strategy)) {
                        onSuccess { navHost.popBackStack() }
                        onFailure { error ->
                            error.printStackTrace()
                            println("failed to create new strategy")
                        }
                    }
                }
            }
        }
    }
}

