package com.ykt.musicplayer.ui.category

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.ykt.musicplayer.ui.home.components.SongGridItem
import com.ykt.musicplayer.ui.home.sections.genre.GenreViewModel
import com.ykt.musicplayer.ui.home.sections.languages.LanguageViewModel
import com.ykt.musicplayer.ui.home.sections.playlists.PlaylistGridItem
import com.ykt.musicplayer.ui.home.sections.playlists.PlaylistsViewModel
import com.ykt.musicplayer.ui.home.sections.recentlyPlayed.RecentlyPlayedViewModel
import com.ykt.musicplayer.ui.home.sections.songs.SongsSectionViewModel
import com.ykt.musicplayer.ui.player.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@UnstableApi
@Composable
fun CategoryDetailScreen(
    categoryId: String,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    songsViewModel: SongsSectionViewModel = hiltViewModel(),
    recentlyPlayedViewModel: RecentlyPlayedViewModel = hiltViewModel(),
    genreViewModel: GenreViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel(),
    playlistsViewModel: PlaylistsViewModel = hiltViewModel()
) {
    val title = when (categoryId) {
        "all_songs" -> "All Songs"
        "recently_played" -> "Recently Played"
        "playlists" -> "Playlists"
        "genres" -> "Genres"
        "languages" -> "Languages"
        else -> "Collection"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (categoryId) {
                "all_songs" -> {
                    val songs by songsViewModel.items.collectAsState()
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(songs) { song ->
                            SongGridItem(
                                song = song,
                                elementKey = "thumbnail_${song.id}_detail",
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                onClick = {
                                    playerViewModel.playSong(song)
                                    navController.navigate("player/${song.id}?elementKey=thumbnail_${song.id}_detail")
                                }
                            )
                        }
                    }
                }
                "recently_played" -> {
                    val recents by recentlyPlayedViewModel.items.collectAsState()
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(recents) { song ->
                            SongGridItem(
                                song = song,
                                elementKey = "thumbnail_${song.id}_detail",
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                onClick = {
                                    playerViewModel.playSong(song)
                                    navController.navigate("player/${song.id}?elementKey=thumbnail_${song.id}_detail")
                                }
                            )
                        }
                    }
                }
                "playlists" -> {
                    val playlists by playlistsViewModel.playlists.collectAsState()
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(playlists) { playlist ->
                            PlaylistGridItem(
                                playlist = playlist,
                                onClick = { /* TODO: Navigate to Playlist Detail */ }
                            )
                        }
                    }
                }
                "genres" -> {
                    val genres by genreViewModel.items.collectAsState()
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 100.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(genres) { genre ->
                            AssistChip(
                                onClick = { /* TODO: Genre filtering */ },
                                label = { Text(genre.name) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                "languages" -> {
                    val languages by languageViewModel.items.collectAsState()
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 100.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(languages) { language ->
                            AssistChip(
                                onClick = { /* TODO: Language filtering */ },
                                label = { Text(language.name) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
