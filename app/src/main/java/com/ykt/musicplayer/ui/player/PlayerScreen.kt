package com.ykt.musicplayer.ui.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Text
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
import com.ykt.musicplayer.ui.player.components.FrostedPanel
import com.ykt.musicplayer.ui.player.components.MusicBar
import com.ykt.musicplayer.utils.PlayerConstants
import com.ykt.musicplayer.utils.PlayerState
import com.ykt.musicplayer.utils.darkenColorFilter
import com.ykt.musicplayer.utils.grayscaleFilter
import com.ykt.musicplayer.utils.rememberDominantColor
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    songId: String,
) {
    val song by viewModel.currentSong.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val position by viewModel.positionMs.collectAsStateWithLifecycle()
    val duration by viewModel.durationMs.collectAsStateWithLifecycle()
    val dominantColor = rememberDominantColor(song?.thumbnailUrl)

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

    LaunchedEffect(songId, position, playerState) {
        showOverlay = false
        delay(PlayerConstants.SCREEN_TIMEOUT)
        showOverlay = true
    }

    LaunchedEffect(songId) {
        if (songId.isNotEmpty()) {
            viewModel.loadSongById(songId)
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
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

            FrostedPanel(
                hazeState = hazeState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(8.dp),
                radius = 32.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    AnimatedContent(
                        targetState = showOverlay,
                        transitionSpec = {
                            fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                        }
                    ) { overlayActive ->
                        AsyncImage(
                            model = song?.thumbnailUrl,
                            contentDescription = song?.title ?: "Artwork",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.0f)
                                .clip(RoundedCornerShape(24.dp))
                                .graphicsLayer {
                                    scaleX = thumbnailScale
                                    scaleY = thumbnailScale
                                },
                            contentScale = ContentScale.Crop,
                            colorFilter = if(overlayActive) grayscaleFilter() else null
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            song?.title.orEmpty(), 
                            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                        Text(
                            song?.artist.orEmpty(), 
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Slider(
                            value = displayed.coerceIn(0f, 1f),
                            onValueChange = { fraction ->
                                isSeeking = true
                                sliderValue = fraction
                            },
                            onValueChangeFinished = {
                                val newPos = (sliderValue * duration).toLong()
                                viewModel.seekTo(newPos)
                                isSeeking = false
                            },
                            track = { state ->
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                ) {
                                    val fraction = state.value
                                    val trackHeight = size.height
                                    val trackWidth = size.width

                                    // Inactive track
                                    drawLine(
                                        color = dominantColor.copy(alpha = 0.3f),
                                        start = Offset(0f, trackHeight / 2),
                                        end = Offset(trackWidth, trackHeight / 2),
                                        strokeWidth = trackHeight,
                                        cap = StrokeCap.Round
                                    )

                                    // Active track
                                    drawLine(
                                        color = dominantColor,
                                        start = Offset(0f, trackHeight / 2),
                                        end = Offset(trackWidth * fraction, trackHeight / 2),
                                        strokeWidth = trackHeight,
                                        cap = StrokeCap.Round
                                    )
                                }
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = dominantColor,
                                activeTrackColor = dominantColor,
                                inactiveTrackColor = dominantColor.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            // Thin track
                            // Circular thumb
//                            thumb = {
//                                Box(
//                                    Modifier
//                                        .size(12.dp)
//                                        .background(dominantColor, CircleShape)
//                                )
//                            }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(formatTime(position), color = MaterialTheme.colorScheme.onBackground)
                            Text(formatTime(duration), color = MaterialTheme.colorScheme.onBackground)
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp), 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val newPos = (position - 10_000).coerceAtLeast(0)
                            viewModel.seekTo(newPos)
                        }) {
                            Icon(
                                Icons.Rounded.Replay10, 
                                contentDescription = "Back 10s", 
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(onClick = { viewModel.togglePlayPause() }) {
                            val icon = when (playerState) {
                                PlayerState.Playing -> {
                                    Icons.Rounded.Pause
                                }
                                PlayerState.Paused, PlayerState.Ended, PlayerState.Idle -> {
                                    Icons.Rounded.PlayArrow
                                }
                                PlayerState.Buffering -> {
                                    Icons.Rounded.HourglassEmpty
                                }
                                PlayerState.Error -> {
                                    Icons.Rounded.Error
                                }
                            }
                            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(48.dp))
                        }

                        IconButton(onClick = {
                            val newPos = (position + 10_000).coerceAtMost(duration)
                            viewModel.seekTo(newPos)
                        }) {
                            Icon(Icons.Rounded.Forward10, contentDescription = "Forward 10s", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }
        }
    }
}

// Helper to format ms → mm:ss
fun formatTime(ms: Long): String {
    val totalSeconds = max(ms, 0L) / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}