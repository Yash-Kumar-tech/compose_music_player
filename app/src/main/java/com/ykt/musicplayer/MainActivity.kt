package com.ykt.musicplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ykt.musicplayer.ui.home.HomeScreen
import com.ykt.musicplayer.ui.player.PlayerScreen
import com.ykt.musicplayer.ui.player.PlayerViewModel
import com.ykt.musicplayer.ui.settings.SettingsScreen
import com.ykt.musicplayer.ui.settings.SettingsViewModel
import com.ykt.musicplayer.ui.theme.MusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val channel = NotificationChannel(
            "music_channel",
            "Music Playback",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Playback controls and song info"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)

        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                val navController = rememberNavController()

                val songId = intent?.getStringExtra("deeplink_song_id")
                LaunchedEffect(songId) {
                    if (!songId.isNullOrEmpty()) {
                        navController.navigate("player/$songId")
                    }
                }

                AppNavHost(navController = navController)
            }
        }
    }
}