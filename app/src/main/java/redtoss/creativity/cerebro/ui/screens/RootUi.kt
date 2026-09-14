package redtoss.creativity.cerebro.ui.screens

import android.util.Log
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
            // Declared once for every destination rather than per route: the transitions
            // used to differ screen by screen, which is what made navigation feel ad hoc.
            enterTransition = { ForwardEnter },
            exitTransition = { ForwardExit },
            popEnterTransition = { BackEnter },
            popExitTransition = { BackExit },
            predictivePopEnterTransition = { swipeEdge -> predictivePopEnter(swipeEdge) },
            predictivePopExitTransition = { swipeEdge -> predictivePopExit(swipeEdge) },
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
            ) {
                LibraryScreen(strategyProvider, navHost)
            }

            composable(
                route = Screens.Category.name,
                arguments = Screens.Category.arguments,
            ) {
                Screens.Category.categoryArgument(it)?.let { category ->
                    CategoryScreen(category = category, strategies = strategies, navHost = navHost)
                } ?: navHost.popBackStack()
            }

            composable(
                route = Screens.Strategy.name,
                arguments = Screens.Strategy.arguments,
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
            ) {
                AboutScreen()
            }

            composable(route = Screens.Settings.name, arguments = Screens.Settings.arguments) {
                val themeMode by appSettings.themeMode.collectAsStateWithLifecycle()
                val colorTheme by appSettings.colorTheme.collectAsStateWithLifecycle()
                SettingsScreen(
                    themeMode = themeMode,
                    colorTheme = colorTheme,
                    onThemeModeSelected = appSettings::setThemeMode,
                    onColorThemeSelected = appSettings::setColorTheme,
                )
            }

            composable(route = Screens.UnlockAll.name, arguments = Screens.UnlockAll.arguments) {
                UnlockAllScreen()
            }

            composable(
                route = Screens.NewStrategy.name,
                arguments = Screens.NewStrategy.arguments,
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
