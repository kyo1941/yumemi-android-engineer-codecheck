package jp.co.yumemi.android.code_check.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        composable(
            "repository/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")?.let { Uri.decode(it) }
                ?: throw IllegalArgumentException("Repository name is required")
            RepositoryScreen(repositoryName = name)
        }
    }
}