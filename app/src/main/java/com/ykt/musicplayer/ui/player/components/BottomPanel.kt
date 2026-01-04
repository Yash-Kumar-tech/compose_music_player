package com.ykt.musicplayer.ui.player.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ykt.musicplayer.utils.PlayerState
import dev.chrisbanes.haze.HazeState

@Composable
fun BottomPanel(
    playerState: PlayerState,
    dominantColor: androidx.compose.ui.graphics.Color,
    onReplay10s: () -> Unit,
    onForward10s: () -> Unit,
    onTogglePlayPause: () -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier
) {
    FrostedPanel(
        hazeState = hazeState,
        modifier = modifier,
        radius = 100.dp,
    ) {
        PlaybackControls(
            playerState = playerState,
            dominantColor = dominantColor,
            onReplay10s = onReplay10s,
            onForward10s = onForward10s,
            onTogglePlayPause = onTogglePlayPause,
            modifier = Modifier.padding(16.dp)
        )
    }
}
