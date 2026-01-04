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
import com.ykt.musicplayer.ui.home.HomeScreen
import com.ykt.musicplayer.ui.player.PlayerScreen
import com.ykt.musicplayer.ui.player.PlayerViewModel
import com.ykt.musicplayer.ui.settings.SettingsScreen
import com.ykt.musicplayer.ui.settings.SettingsViewModel

object Routes {
    const val HOME = "home"
    const val PLAYER = "player/{songId}?elementKey={elementKey}"
    const val SETTINGS = "settings"
}

@OptIn(UnstableApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(navController: NavHostController) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    playerViewModel = hiltViewModel(),
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
                    viewModel = hiltViewModel<PlayerViewModel>(),
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
                    playerViewModel = hiltViewModel<PlayerViewModel>(),
                    navController = navController
                )
            }
        }
    }
}
