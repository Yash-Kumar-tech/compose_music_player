package com.ykt.musicplayer.ui.navigation
//
//import androidx.compose.animation.ExperimentalAnimationApi
//import androidx.compose.animation.SharedTransitionLayout
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.composable
//import com.google.accompanist.navigation.animation.AnimatedNavHost
//import com.ykt.musicplayer.ui.home.HomeScreen
//import com.ykt.musicplayer.ui.player.PlayerScreen
//
//@Suppress("DEPRECATION")
//@OptIn(ExperimentalAnimationApi::class)
//@Composable
//fun AppNavHost(navController: NavHostController) {
//    SharedTransitionLayout {
//        AnimatedNavHost(
//            navController = navController,
//            startDestination = "home",
//        ) {
//            composable("home") {
//                HomeScreen(
//                    navController = navController,
//                    sharedTransitionScope = this@SharedTransitionLayout
//                )
//            }
//            composable("player/{songId}") { backStackEntry ->
//                val songId = backStackEntry.arguments?.getString("songId") ?: ""
//                PlayerScreen(
//                    songId = songId,
//                    sharedTransitionScope = this@SharedTransitionLayout,
//                    onClose = { navController.popBackStack() }
//                )
//            }
//
//        }
//    }
//}