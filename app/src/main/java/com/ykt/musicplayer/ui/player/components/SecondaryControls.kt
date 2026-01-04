package com.ykt.musicplayer.ui.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Shuffle
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
    onLikeClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onAddClick: () -> Unit,
    dominantColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onShuffleClick) {
            Icon(Icons.Rounded.Shuffle, contentDescription = "Shuffle", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
        IconButton(onClick = onLikeClick) {
            Icon(Icons.Rounded.FavoriteBorder, contentDescription = "Like", tint = if (isLiked) dominantColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
        IconButton(onClick = onRepeatClick) {
            Icon(Icons.Rounded.Repeat, contentDescription = "Repeat", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
        IconButton(onClick = onAddClick) {
            Icon(Icons.Rounded.PlaylistAdd, contentDescription = "Add to playlist", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}
