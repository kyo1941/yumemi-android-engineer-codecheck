package jp.co.yumemi.android.code_check.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import jp.co.yumemi.android.code_check.domain.model.Item
import jp.co.yumemi.android.code_check.ui.OneScreen
import jp.co.yumemi.android.code_check.ui.RepositoryScreen
import kotlin.text.get

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
                ?: throw IllegalArgumentException("Repository item is required")
            RepositoryScreen(item = item)
        }
    }
}