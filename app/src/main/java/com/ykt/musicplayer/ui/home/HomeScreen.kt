package com.ykt.musicplayer.ui.home

import PlaylistsSection
import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.ykt.musicplayer.ui.home.sections.playlists.PlaylistsViewModel
import com.ykt.musicplayer.ui.home.sections.songs.SongsSection
import com.ykt.musicplayer.ui.home.sections.songs.SongsSectionViewModel
import com.ykt.musicplayer.ui.player.PlayerViewModel
import com.ykt.musicplayer.ui.player.components.MusicBar
import com.ykt.musicplayer.ui.player.components.CreatePlaylistDialog
import com.ykt.musicplayer.utils.rememberDominantColor
import androidx.compose.runtime.setValue

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    songsViewModel: SongsSectionViewModel = hiltViewModel(),
    recentlyPlayedViewModel: RecentlyPlayedViewModel = hiltViewModel(),
    genreViewModel: GenreViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel(),
    playlistsViewModel: PlaylistsViewModel = hiltViewModel()
) {
    val sections by viewModel.sections.collectAsState()
    val songs by songsViewModel.items.collectAsState()
    val recents by recentlyPlayedViewModel.items.collectAsState()
    val loadedGenres by genreViewModel.items.collectAsState()
    val loadedLanguages by languageViewModel.items.collectAsState()
    val playlists by playlistsViewModel.playlists.collectAsState()

    val songsExpanded by songsViewModel.isExpanded.collectAsState()
    val recentsExpanded by recentlyPlayedViewModel.isExpanded.collectAsState()
    val playlistsExpanded by playlistsViewModel.isExpanded.collectAsState()

    var showNewPlaylistDialog by remember { mutableStateOf(false) }
    val currentSong by playerViewModel.currentSong.collectAsState()
    val dominantColor = rememberDominantColor(currentSong?.thumbnailUrl)


    val loading by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Music Player",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding()),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 1.dp
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 24.dp,
                            bottom = 120.dp,
                            start = 8.dp,
                            end = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        items(sections) { section ->
                            when (section.id) {
                                "all_songs" -> SongsSection(
                                    section.title,
                                    sectionId = section.id,
                                    songs,
                                    isExpanded = songsExpanded,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    onSongClick = { song ->
                                        val elementKey = "thumbnail_${song.id}_${section.id}"
                                        playerViewModel.playSong(song)
                                        navController.navigate("player/${song.id}?elementKey=$elementKey")
                                    },
                                    onShowAllClick = {
                                        navController.navigate("category/${section.id}")
                                    }
                                )

                                "recently_played" -> RecentlyPlayedSection(
                                    section.title,
                                    sectionId = section.id,
                                    recents,
                                    isExpanded = recentsExpanded,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    onSongClick = { song ->
                                        val elementKey = "thumbnail_${song.id}_${section.id}"
                                        playerViewModel.playSong(song)
                                        navController.navigate("player/${song.id}?elementKey=$elementKey")
                                    },
                                    onShowAllClick = {
                                        navController.navigate("category/${section.id}")
                                    }
                                )

                                "genres" -> GenreSection(
                                    section.title,
                                    loadedGenres,
                                    onGenreClick = { genre ->
                                        Log.d("HomeScreen", "Tapped genre ${genre.name}")
                                    },
                                    onShowAllClick = {
                                        navController.navigate("category/${section.id}")
                                    }
                                )

                                "languages" -> LanguageSection(
                                    section.title,
                                    loadedLanguages,
                                    onLanguageClick = { language ->
                                        Log.d("HomeScreen", "Tapped language ${language.name}")
                                    },
                                    onShowAllClick = {
                                        navController.navigate("category/${section.id}")
                                    }
                                )

                                "playlists" -> PlaylistsSection(
                                    section.title,
                                    playlists,
                                    isExpanded = playlistsExpanded,
                                    onPlaylistClick = { playlist ->
                                        Log.d("HomeScreen", "Tapped playlist ${playlist.name}")
                                    },
                                    onCreateClick = {
                                        showNewPlaylistDialog = true
                                    },
                                    onShowAllClick = {
                                        navController.navigate("category/${section.id}")
                                    }
                                )

                                else -> Text(section.title, modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }

                MusicBar(
                    viewModel = playerViewModel,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onExpand = {
                        val currentSong = playerViewModel.currentSong.value
                        val songId = currentSong?.id
                        if (!songId.isNullOrEmpty()) {
                            val elementKey = "thumbnail_${songId}_bar"
                            navController.navigate("player/$songId?elementKey=$elementKey")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                )
            }

            if (showNewPlaylistDialog) {
                CreatePlaylistDialog(
                    hazeState = null, // No haze needed on home screen for now
                    dominantColor = dominantColor,
                    onDismissRequest = { showNewPlaylistDialog = false },
                    onCreateClick = { name ->
                        playerViewModel.createPlaylist(name)
                        showNewPlaylistDialog = false
                    }
                )
            }
        }
    }
}
