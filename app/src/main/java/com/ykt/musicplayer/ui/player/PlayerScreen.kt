package com.ykt.musicplayer.ui.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ykt.musicplayer.ui.player.components.AlbumArt
import com.ykt.musicplayer.ui.player.components.FrostedPanel
import com.ykt.musicplayer.ui.player.components.MusicBar
import com.ykt.musicplayer.ui.player.components.MusicVisualizer
import com.ykt.musicplayer.ui.player.components.PlaybackControls
import com.ykt.musicplayer.ui.player.components.PlaybackSlider
import com.ykt.musicplayer.ui.player.components.SecondaryControls
import com.ykt.musicplayer.ui.player.components.SongDetails
import com.ykt.musicplayer.utils.PlayerConstants
import com.ykt.musicplayer.utils.PlayerState
import com.ykt.musicplayer.utils.darkenColorFilter
import com.ykt.musicplayer.ui.player.components.PlaylistSheet
import com.ykt.musicplayer.ui.player.components.CreatePlaylistDialog
import com.ykt.musicplayer.ui.player.components.TopPanel
import com.ykt.musicplayer.ui.player.components.BottomPanel
import com.ykt.musicplayer.utils.rememberDominantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    songId: String,
    elementKey: String = "",
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val song by viewModel.currentSong.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val position by viewModel.positionMs.collectAsStateWithLifecycle()
    val duration by viewModel.durationMs.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val dominantColor = rememberDominantColor(song?.thumbnailUrl)

    var showPlaylistSheet by remember { mutableStateOf(false) }
    var showNewPlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val grayscaleDominantColor = remember(dominantColor) {
        val luminance =
            0.299f * dominantColor.red + 0.587f * dominantColor.green + 0.114f * dominantColor.blue
        Color(luminance, luminance, luminance, dominantColor.alpha)
    }

    val hazeState = rememberHazeState()

    var isSeeking by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(0f) }
    val progress = if (duration > 0) position.toFloat() / duration else 0f
    val displayed = if (isSeeking) sliderValue else progress
    var showOverlay by remember { mutableStateOf(false) }
    val darkness by animateFloatAsState(
        targetValue = if (showOverlay) 1f else 0f,
        animationSpec = tween(durationMillis = 600)
    )
    val thumbnailScale by animateFloatAsState(
        targetValue = if (showOverlay) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 500)
    )

    val animatedDominantColor by animateColorAsState(
        targetValue = if (showOverlay) grayscaleDominantColor else dominantColor,
        animationSpec = tween(durationMillis = 600),
        label = "DominantColorAnimation"
    )

    // Overlay timer: only reset when song changes, user interacts, or playback starts
    var interactionSource by remember { mutableLongStateOf(0L) }
    LaunchedEffect(songId, interactionSource, playerState) {
        if (playerState == PlayerState.Playing) {
            showOverlay = false
            delay(settings.screenTimeoutMs)
            showOverlay = true
        } else {
            showOverlay = false
        }
    }

    LaunchedEffect(songId) {
        if (songId.isNotEmpty()) {
            viewModel.loadSongById(songId)
        }
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(dominantColor) // Stable background color
                .pointerInput(Unit) {
                    detectTapGestures {
                        interactionSource = System.currentTimeMillis()
                        showOverlay = false
                    }
                }
        ) {
            AsyncImage(
                model = song?.thumbnailUrl,
                contentDescription = song?.title ?: "Artwork",
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState),
                contentScale = ContentScale.Crop,
                colorFilter = darkenColorFilter(darkness)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TopPanel(
                    song = song,
                    playerState = playerState,
                    dominantColor = dominantColor,
                    animatedDominantColor = animatedDominantColor,
                    position = position,
                    duration = duration,
                    displayedProgress = displayed,
                    showOverlay = showOverlay,
                    thumbnailScale = thumbnailScale,
                    hazeState = hazeState,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    elementKey = elementKey,
                    onAddClick = { showPlaylistSheet = true },
                    onValueChange = { fraction ->
                        isSeeking = true
                        sliderValue = fraction
                    },
                    onValueChangeFinished = {
                        val newPos = (sliderValue * duration).toLong()
                        viewModel.seekTo(newPos)
                        isSeeking = false
                    },
                    modifier = Modifier.weight(1f)
                )

                BottomPanel(
                    playerState = playerState,
                    dominantColor = animatedDominantColor,
                    onReplay10s = {
                        val newPos = (position - 10_000).coerceAtLeast(0)
                        viewModel.seekTo(newPos)
                    },
                    onForward10s = {
                        val newPos = (position + 10_000).coerceAtMost(duration)
                        viewModel.seekTo(newPos)
                    },
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    hazeState = hazeState,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            if (darkness > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f * darkness))
                        .pointerInput(Unit) {
                            detectTapGestures {
                                interactionSource = System.currentTimeMillis()
                                showOverlay = false
                            }
                        }
                )
            }
        }
    }

    if (showPlaylistSheet) {
        PlaylistSheet(
            playlists = playlists,
            hazeState = hazeState,
            dominantColor = dominantColor,
            sheetState = sheetState,
            onDismissRequest = { showPlaylistSheet = false },
            onPlaylistSelect = { playlist ->
                song?.id?.let { songId ->
                    viewModel.addSongToPlaylist(playlist.id, songId)
                }
                showPlaylistSheet = false
            },
            onCreateNewClick = {
                showPlaylistSheet = false
                showNewPlaylistDialog = true
            }
        )
    }

    if (showNewPlaylistDialog) {
        CreatePlaylistDialog(
            hazeState = hazeState,
            dominantColor = dominantColor,
            onDismissRequest = { showNewPlaylistDialog = false },
            onCreateClick = { name ->
                viewModel.createPlaylist(name)
                showNewPlaylistDialog = false
            }
        )
    }
}

// Helper to format ms → mm:ss
fun formatTime(ms: Long): String {
    val totalSeconds = max(ms, 0L) / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}