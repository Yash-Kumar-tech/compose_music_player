package com.ykt.musicplayer

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.ykt.musicplayer.ui.home.HomeScreen
import com.ykt.musicplayer.ui.player.PlayerScreen
import com.ykt.musicplayer.ui.player.PlayerViewModel
import com.ykt.musicplayer.ui.settings.SettingsScreen
import com.ykt.musicplayer.ui.settings.SettingsViewModel
import com.ykt.musicplayer.ui.category.CategoryDetailScreen

object Routes {
    const val HOME = "home"
    const val PLAYER = "player/{songId}?elementKey={elementKey}"
    const val SETTINGS = "settings"
    const val CATEGORY_DETAIL = "category/{categoryId}"
}

@OptIn(UnstableApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(navController: NavHostController) {
    SharedTransitionLayout {
        val playerViewModel: PlayerViewModel = hiltViewModel()
        
        NavHost(
            navController = navController,
            startDestination = Routes.HOME
        ) {
            composable(
                route = Routes.HOME,
                enterTransition = {
                    fadeIn(animationSpec = tween(700))
                },
                exitTransition = {
                    if (targetState.destination.route == Routes.SETTINGS) {
                        slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                    } else {
                        fadeOut(animationSpec = tween(500))
                    }
                },
                popEnterTransition = {
                    if (initialState.destination.route == Routes.SETTINGS) {
                        slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                    } else {
                        fadeIn(animationSpec = tween(500))
                    }
                }
            ) {
                HomeScreen(
                    playerViewModel = playerViewModel,
                    navController = navController,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
            composable(
                route = Routes.PLAYER,
                arguments = listOf(
                    navArgument("songId") { type = NavType.StringType },
                    navArgument("elementKey") { 
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val songId = backStackEntry.arguments?.getString("songId") ?: ""
                val elementKey = backStackEntry.arguments?.getString("elementKey") ?: "thumbnail_${songId}_card"
                PlayerScreen(
                    viewModel = playerViewModel,
                    songId = songId,
                    elementKey = elementKey,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
            composable(
                route = Routes.SETTINGS,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                }
            ) {
                SettingsScreen(
                    viewModel = hiltViewModel<SettingsViewModel>(),
                    playerViewModel = playerViewModel,
                    navController = navController
                )
            }
            composable(
                route = Routes.CATEGORY_DETAIL,
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                CategoryDetailScreen(
                    categoryId = categoryId,
                    navController = navController,
                    playerViewModel = playerViewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
        }
    }
}
