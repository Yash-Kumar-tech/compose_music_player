package com.ykt.musicplayer.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ykt.musicplayer.ui.home.sections.genre.GenreSection
import com.ykt.musicplayer.ui.home.sections.genre.GenreViewModel
import com.ykt.musicplayer.ui.home.sections.languages.LanguageSection
import com.ykt.musicplayer.ui.home.sections.languages.LanguageViewModel
import com.ykt.musicplayer.ui.home.sections.recentlyPlayed.RecentlyPlayedSection
import com.ykt.musicplayer.ui.home.sections.recentlyPlayed.RecentlyPlayedViewModel
import com.ykt.musicplayer.ui.home.sections.songs.SongsSection
import com.ykt.musicplayer.ui.home.sections.songs.SongsSectionViewModel
import com.ykt.musicplayer.ui.player.PlayerViewModel
import com.ykt.musicplayer.ui.player.components.MusicBar

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    songsViewModel: SongsSectionViewModel = hiltViewModel(),
    recentlyPlayedViewModel: RecentlyPlayedViewModel = hiltViewModel(),
    genreViewModel: GenreViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val sections by viewModel.sections.collectAsState()
    val songs by songsViewModel.items.collectAsState()
    val recents by recentlyPlayedViewModel.items.collectAsState()
    val genres by genreViewModel.items.collectAsState()
    val languages by languageViewModel.items.collectAsState()

    val loading by remember { mutableStateOf(false)}

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Custom Music Player") },
                    actions = {
                        IconButton(
                            onClick = {
                                navController.navigate("settings")
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Settings,
                                contentDescription = "Settings",
                            )
                        }
                    }
                )
            },
        ) { innerPadding ->
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    items(sections) { section ->
                        when (section.id) {
                            "all_songs" -> SongsSection(
                                section.title,
                                songs,
                                onSongClick = { song ->
                                    playerViewModel.playSong(song)
                                    navController.navigate("player/${song.id}")
                                }
                            )
                            "recently_played" -> RecentlyPlayedSection(
                                section.title,
                                recents,
                                onSongClick = { song ->
                                    playerViewModel.playSong(song)
                                    navController.navigate("player/${song.id}")
                                }
                            )
                            "genres" -> GenreSection(
                                section.title,
                                genres,
                                onGenreClick = { genre ->
                                    Log.d("HomeScreen", "Tapped genre ${genre.name}")
                                }
                            )
                            "languages" -> LanguageSection(
                                section.title,
                                languages,
                                onLanguageClick = { language ->
                                    Log.d("HomeScreen", "Tapped language ${language.name}")
                                }
                            )
                            else -> Text(section.title)
                        }
                    }
                }

            }
        }
        MusicBar(
            viewModel = playerViewModel,
            onExpand = {
                val songId = playerViewModel.currentSong.value?.id
                if (!songId.isNullOrEmpty()) {
                    navController.navigate("player/$songId")
                }
           },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}