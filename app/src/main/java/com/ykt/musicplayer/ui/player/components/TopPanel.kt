package com.ykt.musicplayer.ui.player.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.utils.PlayerState
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TopPanel(
    song: Song?,
    playerState: PlayerState,
    dominantColor: Color,
    animatedDominantColor: Color,
    position: Long,
    duration: Long,
    displayedProgress: Float,
    showOverlay: Boolean,
    thumbnailScale: Float,
    hazeState: HazeState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    elementKey: String,
    onAddClick: () -> Unit,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    FrostedPanel(
        hazeState = hazeState,
        modifier = modifier,
        radius = 32.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AlbumArt(
                thumbnailUrl = song?.thumbnailUrl,
                title = song?.title,
                songId = song?.id.orEmpty(),
                elementKey = elementKey,
                showOverlay = showOverlay,
                thumbnailScale = thumbnailScale,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth()
            )

            MusicVisualizer(
                playerState = playerState,
                color = animatedDominantColor,
                modifier = Modifier.padding(top = 16.dp)
            )

            SongDetails(
                title = song?.title,
                artist = song?.artist,
                modifier = Modifier.padding(top = 16.dp)
            )

            SecondaryControls(
                isLiked = false, // TODO: Link with VM
                onLikeClick = {},
                onShuffleClick = {},
                onRepeatClick = {},
                onAddClick = onAddClick,
                dominantColor = animatedDominantColor,
                modifier = Modifier.padding(top = 16.dp)
            )

            PlaybackSlider(
                position = position,
                duration = duration,
                displayedProgress = displayedProgress,
                animatedDominantColor = animatedDominantColor,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}
