package com.ykt.musicplayer.ui.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SecondaryControls(
    isLiked: Boolean,
    isShuffle: Boolean,
    isRepeat: Boolean,
    isAutoplay: Boolean,
    isFromPlaylist: Boolean,
    onLikeClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onAutoplayClick: () -> Unit,
    onAddClick: () -> Unit,
    dominantColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onShuffleClick) {
            Icon(
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = "Shuffle",
                tint = if (isShuffle) dominantColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
        
        IconButton(onClick = onAutoplayClick) {
            Icon(
                imageVector = Icons.Rounded.AutoMode,
                contentDescription = "Autoplay",
                tint = if (isAutoplay) dominantColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        IconButton(onClick = onRepeatClick) {
            Icon(
                imageVector = Icons.Rounded.Repeat,
                contentDescription = "Repeat",
                tint = if (isRepeat) dominantColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        IconButton(onClick = onAddClick) {
            Icon(
                imageVector = if (isFromPlaylist) Icons.Rounded.PlaylistPlay else Icons.Rounded.PlaylistAdd,
                contentDescription = "Playlist",
                tint = if (isFromPlaylist) dominantColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "Like",
                tint = if (isLiked) dominantColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}
