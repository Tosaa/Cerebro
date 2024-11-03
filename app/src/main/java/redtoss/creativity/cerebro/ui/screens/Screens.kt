package redtoss.creativity.cerebro.ui.screens

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.serialization.json.Json
import redtoss.creativity.cerebro.data.serialized

public fun NavHostController.navigateToScreen(screen: Screens) {
    navigate(route = screen.route)
}

sealed interface Screens {
    val name: String
        get() = this.javaClass.simpleName

    val route: String
        get() = name

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    object Main : Screens

    class Category(val category: redtoss.creativity.cerebro.data.Category) : Screens {
        override val route: String
            get() = "category/${category.name}"

        companion object : Screens {
            override val name: String
                get() = "category/{category}"

            override val arguments: List<NamedNavArgument>
                get() = listOf(navArgument("category") { type = NavType.StringType })

            fun categoryArgument(it: NavBackStackEntry): redtoss.creativity.cerebro.data.Category? {
                return it.arguments?.getString("category")?.let { redtoss.creativity.cerebro.data.Category.valueOf(it) }
            }
        }
    }


    class Strategy(val strategy: redtoss.creativity.cerebro.data.Strategy) : Screens {
        override val route: String
            get() = "strategy/${strategy.hashCode()}"


        companion object : Screens {
            override val name: String
                get() = "strategy/{strategy}"

            override val arguments: List<NamedNavArgument>
                get() = listOf(navArgument("strategy") {
                    type = NavType.IntType
                })

            fun strategyArgument(it: NavBackStackEntry): Int? {
                return it.arguments?.getInt("strategy")
            }
        }
    }

    object StrategyLibrary : Screens {
        override val route: String
            get() = name

        override val arguments: List<NamedNavArgument> = emptyList()

    }

    object NewStrategy : Screens {
        override val route: String
            get() = name
        override val arguments: List<NamedNavArgument> = emptyList()
    }

    object About : Screens {
        override val route: String
            get() = name

        override val arguments: List<NamedNavArgument> = emptyList()
    }
}
