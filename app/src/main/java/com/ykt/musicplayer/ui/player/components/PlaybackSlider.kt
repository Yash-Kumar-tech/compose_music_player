package com.ykt.musicplayer.ui.player.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackSlider(
    position: Long,
    duration: Long,
    displayedProgress: Float,
    animatedDominantColor: Color,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = displayedProgress.coerceIn(0f, 1f),
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
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
                        color = animatedDominantColor.copy(alpha = 0.3f),
                        start = Offset(0f, trackHeight / 2),
                        end = Offset(trackWidth, trackHeight / 2),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round
                    )

                    // Active track
                    drawLine(
                        color = animatedDominantColor,
                        start = Offset(0f, trackHeight / 2),
                        end = Offset(trackWidth * fraction, trackHeight / 2),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round
                    )
                }
            },
            colors = SliderDefaults.colors(
                thumbColor = animatedDominantColor,
                activeTrackColor = animatedDominantColor,
                inactiveTrackColor = animatedDominantColor.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth(),
            thumb = {
                Box(
                    Modifier
                        .size(4.dp)
                        .background(animatedDominantColor, CircleShape)
                )
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(position), color = MaterialTheme.colorScheme.onBackground)
            Text(formatTime(duration), color = MaterialTheme.colorScheme.onBackground)
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
