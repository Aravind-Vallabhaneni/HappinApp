package com.example.happinapp.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.happinapp.screens.LoginSignupScreen
import com.example.happinapp.screens.HomeScreen
import com.example.happinapp.screens.LocationSelectionScreen
import com.example.happinapp.screens.MovieDetailScreen
import com.example.happinapp.screens.ProfileScreen
import com.example.happinapp.screens.SignupScreen
import com.example.happinapp.viewmodels.HomeViewModel

sealed class Screen(val route: String) {
    object LoginSignup : Screen("login_signup_screen")
    object Home : Screen("home_screen/{uid}") {
        fun createRoute(uid: String) = "home_screen/$uid"
    }
    object Signup : Screen("signup_screen")
    object Location : Screen("location_screen")
    object Profile : Screen("profile_screen")
    object MovieDetail : Screen("movie_detail_screen/{movieId}") {
        fun createRoute(movieId: String) = "movie_detail_screen/$movieId"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ... (LoginSignupScreen, HomeScreen, SignupScreen, and LocationScreen composables remain the same)

        composable(Screen.LoginSignup.route) {
            LoginSignupScreen(
                onLoginSuccess = { firebaseUid ->
                    navController.navigate(Screen.Home.createRoute(firebaseUid)) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onSignUpSuccess = { navController.navigate(Screen.Signup.route) }
            )
        }

        composable(
            route = Screen.Home.route,
            arguments = listOf(navArgument("uid") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid")
            HomeScreen(
                loggedInUid = uid,
                navController = navController,
                onLogout = {
                    navController.navigate(Screen.LoginSignup.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Location.route) {
            val homeViewModel: HomeViewModel = viewModel()
            LocationSelectionScreen(
                onCitySelected = { city ->
                    homeViewModel.saveLocation(city.name)
                    navController.popBackStack()
                },
                onClose = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Profile.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    // MODIFIED: Increased duration for a smoother animation
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    // MODIFIED: Increased duration for a smoother animation
                    animationSpec = tween(500)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    // MODIFIED: Increased duration for a smoother animation
                    animationSpec = tween(500)
                )
            },
            // This is for when you navigate back TO the screen below the profile screen
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    // MODIFIED: Increased duration for a smoother animation
                    animationSpec = tween(500)
                )
            }
        ) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.MovieDetail.route,
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")
            if (movieId != null) {
                MovieDetailScreen( // This calls the composable from Step 1
                    movieId = movieId,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                Text("Error: Movie ID missing. Please go back.")
            }
        }
    }
}