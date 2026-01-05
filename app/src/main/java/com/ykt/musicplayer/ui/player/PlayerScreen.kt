package com.ykt.musicplayer.ui.player

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ykt.musicplayer.domain.model.Song
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
        if (playerState == PlayerState.Playing && settings.screenTimeoutMs > 0L) {
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

    SharedTransitionLayout {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Dedicated haze source container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(dominantColor)
                    .hazeSource(hazeState)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "BackgroundAnimation")
                val panOffset by infiniteTransition.animateFloat(
                    initialValue = -15f,
                    targetValue = 15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 15000, easing = androidx.compose.animation.core.LinearEasing),
                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                    ),
                    label = "PanAnimation"
                )

                AsyncImage(
                    model = song?.thumbnailUrl,
                    contentDescription = song?.title ?: "Artwork",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = 1.15f,
                            scaleY = 1.15f,
                            translationX = panOffset,
                            translationY = panOffset / 2
                        ),
                    contentScale = ContentScale.Crop,
                    colorFilter = darkenColorFilter(darkness)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TopPanel(
                    song = song,
                    playerState = playerState,
                    isShuffle = settings.shuffle,
                    isRepeat = settings.looping,
                    isAutoplay = settings.autoplay,
                    isFromPlaylist = viewModel.audioPlayer.mediaItemCount > 1,
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
                    onShuffleClick = { viewModel.toggleShuffle() },
                    onRepeatClick = { viewModel.toggleLooping() },
                    onAutoplayClick = { viewModel.toggleAutoPlay() },
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