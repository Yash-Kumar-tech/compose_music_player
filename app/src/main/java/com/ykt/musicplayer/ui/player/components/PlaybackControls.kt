package com.ykt.musicplayer.ui.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ykt.musicplayer.utils.PlayerState

@Composable
fun PlaybackControls(
    playerState: PlayerState,
    dominantColor: Color,
    onReplay10s: () -> Unit,
    onForward10s: () -> Unit,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onReplay10s) {
            Icon(
                imageVector = Icons.Rounded.Replay10,
                contentDescription = "Back 10s",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(
            onClick = onTogglePlayPause,
            modifier = Modifier
                .size(64.dp)
                .background(dominantColor, CircleShape)
        ) {
            val icon = when (playerState) {
                PlayerState.Playing -> Icons.Rounded.Pause
                PlayerState.Paused, PlayerState.Ended, PlayerState.Idle -> Icons.Rounded.PlayArrow
                PlayerState.Buffering -> Icons.Rounded.HourglassEmpty
                PlayerState.Error -> Icons.Rounded.Error
            }
            // Better contrast check
            val iconColor = if (0.299 * dominantColor.red + 0.587 * dominantColor.green + 0.114 * dominantColor.blue > 0.5) {
                Color.Black.copy(alpha = 0.8f)
            } else {
                Color.White
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
        }

        IconButton(onClick = onForward10s) {
            Icon(
                imageVector = Icons.Rounded.Forward10,
                contentDescription = "Forward 10s",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
