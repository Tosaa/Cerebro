package redtoss.creativity.cerebro.ui.screens

import android.util.Log
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import redtoss.creativity.cerebro.data.AppSettings
import redtoss.creativity.cerebro.data.StrategyProvider
import redtoss.creativity.cerebro.ui.layouts.LoadingState

@Composable
fun AppUi(strategyProvider: StrategyProvider, appSettings: AppSettings) {
    val strategies = strategyProvider.resolvedStrategies.collectAsStateWithLifecycle(null)

    val navHost = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { AppBar(navHost) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        NavHost(
            navController = navHost,
            startDestination = Screens.Main.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            composable(Screens.Main.name) {
                HomeScreen(strategies, navHost)
            }

            composable(
                route = Screens.StrategyLibrary.name,
                arguments = Screens.StrategyLibrary.arguments,
                enterTransition = { slideInVertically { it } },
                exitTransition = { slideOutVertically { it } },
                popEnterTransition = { slideInVertically { it } },
                popExitTransition = { slideOutVertically { it } },
            ) {
                LibraryScreen(strategyProvider, navHost)
            }

            composable(
                route = Screens.Category.name,
                arguments = Screens.Category.arguments,
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { -it } },
                popEnterTransition = { slideInHorizontally { -it } },
                popExitTransition = { slideOutHorizontally { it } },
            ) {
                Screens.Category.categoryArgument(it)?.let { category ->
                    CategoryScreen(category = category, strategies = strategies, navHost = navHost)
                } ?: navHost.popBackStack()
            }

            composable(
                route = Screens.Strategy.name,
                arguments = Screens.Strategy.arguments,
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { -it } },
                popEnterTransition = { slideInHorizontally { -it } },
                popExitTransition = { slideOutHorizontally { it } },
            ) { navBackStackEntry ->
                val loadedStrategies = strategies.value
                val strategyHashCode = Screens.Strategy.strategyArgument(navBackStackEntry)
                if (loadedStrategies == null) {
                    LoadingState()
                } else {
                    val strategy = loadedStrategies.firstOrNull { it.hashCode() == strategyHashCode }
                    strategy?.let { StrategyScreen(it) } ?: navHost.popBackStack()
                }
            }

            composable(
                route = Screens.About.name,
                arguments = Screens.About.arguments,
                enterTransition = { slideInVertically { it } },
                exitTransition = { slideOutVertically { it } },
                popEnterTransition = { slideInVertically { it } },
                popExitTransition = { slideOutVertically { it } },
            ) {
                AboutScreen()
            }

            composable(route = Screens.Settings.name, arguments = Screens.Settings.arguments) {
                val themeMode by appSettings.themeMode.collectAsStateWithLifecycle()
                SettingsScreen(themeMode = themeMode, onThemeModeSelected = appSettings::setThemeMode)
            }

            composable(route = Screens.UnlockAll.name, arguments = Screens.UnlockAll.arguments) {
                UnlockAllScreen()
            }

            composable(
                route = Screens.NewStrategy.name,
                arguments = Screens.NewStrategy.arguments,
                enterTransition = { slideInVertically { it } },
                exitTransition = { slideOutVertically { it } },
                popEnterTransition = { slideInVertically { it } },
                popExitTransition = { slideOutVertically { it } },
            ) {
                StrategyEditorScreen { strategy ->
                    with(strategyProvider.addCustomStrategy(strategy)) {
                        onSuccess { navHost.popBackStack() }
                        onFailure { error ->
                            Log.e(TAG, "Could not save the new strategy", error)
                            scope.launch {
                                snackbarHostState.showSnackbar("Could not save the strategy. Please try again.")
                            }
                        }
                    }
                }
            }
        }
    }
}

private const val TAG = "RootUi"
