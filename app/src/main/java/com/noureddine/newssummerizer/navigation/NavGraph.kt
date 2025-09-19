package com.noureddine.newssummerizer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.noureddine.newssummerizer.ui.theme.screens.HomeSecreen
import com.noureddine.newssummerizer.ui.theme.screens.NewsSecreen
import com.noureddine.newssummerizer.ui.theme.screens.SplashScreen
import com.noureddine.newssummerizer.viewModel.SharedViewModel
import com.noureddine.newssummerizer.viewModel.newsViewModelRoomDb

@Composable
fun NavigationFromSplashScreentoHomeSecreen(sharedViewModel: SharedViewModel, roomViewModel: newsViewModelRoomDb,) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true } // يزيل splash من backstack
                    }
                }
            )
        }
        composable("home") {
            //val roomArticles by roomViewModel.articlesPreProcessing.observeAsState(emptyList())
            val roomArticles by roomViewModel.responseGetArticlesAfterProcessing.observeAsState(emptyList())
            val textLoading by sharedViewModel.responseData.observeAsState("")
            val loading by sharedViewModel.loadingData.observeAsState(false)

            HomeSecreen(
                textLoading = textLoading.toString(),
                loading = loading,
                articles = roomArticles,
                onNavigateToDetails = { id ->
                    navController.navigate("details/$id")
                }
            )
        }

        composable(
            "details/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            val articles by roomViewModel.responseGetArticlesAfterProcessing.observeAsState(emptyList())
            val article = itemId?.toIntOrNull()?.let { idInt ->
                articles?.firstOrNull { it.id == idInt }
            }
            NewsSecreen(
                articleAfterProcessing = article,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
