package jp.co.yumemi.android.code_check.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.co.yumemi.android.code_check.domain.model.Item
import jp.co.yumemi.android.code_check.ui.OneScreen
import jp.co.yumemi.android.code_check.ui.RepositoryScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "one"
    ) {
        composable("one") {
            OneScreen(
                navController = navController,
            )
        }
        composable("repository") {
            val item = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Item>("item")
            LaunchedEffect(Unit) {
                if (item == null) {
                    navController.popBackStack("one", inclusive = false)
                }
            }
            if (item != null) {
                RepositoryScreen(item = item)
            }

        }
    }
}