package com.ykt.musicplayer.ui.player.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ykt.musicplayer.utils.grayscaleFilter

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AlbumArt(
    thumbnailUrl: String?,
    title: String?,
    songId: String,
    elementKey: String = "",
    showOverlay: Boolean,
    thumbnailScale: Float,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val saturation by animateFloatAsState(
        targetValue = if (showOverlay) 0f else 1f,
        animationSpec = tween(600),
        label = "AlbumArtSaturation"
    )

    with(sharedTransitionScope) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = title ?: "Artwork",
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1.0f)
                .sharedElement(
                    rememberSharedContentState(key = elementKey),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .clip(RoundedCornerShape(24.dp))
                .graphicsLayer {
                    scaleX = thumbnailScale
                    scaleY = thumbnailScale
                },
            contentScale = ContentScale.Crop,
            colorFilter = grayscaleFilter(saturation)
        )
    }
}
