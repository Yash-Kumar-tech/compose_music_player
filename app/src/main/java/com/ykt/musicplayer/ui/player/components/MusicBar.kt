package com.ykt.musicplayer.ui.player.components

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.Transformation
import com.commit451.coiltransformations.BlurTransformation
import com.ykt.musicplayer.ui.player.PlayerViewModel
import com.ykt.musicplayer.utils.PlayerState
import kotlinx.coroutines.delay


@OptIn(UnstableApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MusicBar(
    viewModel: PlayerViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val song by viewModel.currentSong.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val position by viewModel.positionMs.collectAsStateWithLifecycle()
    val duration by viewModel.durationMs.collectAsStateWithLifecycle()

    if (song != null) {
        Surface(
            tonalElevation = 4.dp,
            shadowElevation = 8.dp,
            shape = CircleShape,
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable { onExpand() }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Blurred thumbnail background
                AsyncImage(
                    model = song?.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                // Foreground content
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Thumbnail (sharp)
                        with(sharedTransitionScope) {
                            AsyncImage(
                                model = song?.thumbnailUrl,
                                contentDescription = song?.title,
                                modifier = Modifier
                                    .size(56.dp)
                                    .sharedElement(
                                        rememberSharedContentState(key = "thumbnail_${song?.id}_bar"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                song?.title.orEmpty(),
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                song?.artist.orEmpty(),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(onClick = { viewModel.togglePlayPause() }) {
                            val icon = when (playerState) {
                                PlayerState.Playing -> Icons.Default.Pause
                                PlayerState.Paused, PlayerState.Ended, PlayerState.Idle -> Icons.Default.PlayArrow
                                PlayerState.Buffering -> Icons.Default.HourglassEmpty
                                PlayerState.Error -> Icons.Default.Error
                            }
                            Icon(imageVector = icon, contentDescription = null, tint = Color.White)
                        }
                    }

                    // Progress bar at bottom
                    LinearProgressIndicator(
                        progress = { if (duration > 0) position.toFloat() / duration else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f),
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                }
            }
        }
    }
}