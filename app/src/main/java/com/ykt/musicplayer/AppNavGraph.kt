package com.ykt.musicplayer

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ykt.musicplayer.ui.home.HomeScreen
import com.ykt.musicplayer.ui.player.PlayerScreen
import com.ykt.musicplayer.ui.player.PlayerViewModel
import com.ykt.musicplayer.ui.settings.SettingsScreen
import com.ykt.musicplayer.ui.settings.SettingsViewModel

object Routes {
    const val HOME = "home"
    const val PLAYER = "player/{songId}"
    const val SETTINGS = "settings"
}

@OptIn(UnstableApi::class)
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                playerViewModel = hiltViewModel(),
                navController = navController,
            )
        }
        composable(
            route = Routes.PLAYER,
            arguments = listOf(navArgument("songId") { type = NavType.StringType })
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId") ?: ""
            PlayerScreen(
                viewModel = hiltViewModel<PlayerViewModel>(),
                songId = songId,
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = hiltViewModel<SettingsViewModel>(),
                navController = navController
            )
        }
    }
}
