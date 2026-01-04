package com.ykt.musicplayer.utils

import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.Coil
import coil.request.ImageRequest

@Composable
fun rememberDominantColor(imageUrl: String?): Color {
    var dominantColor by remember { mutableStateOf(Color.White) }
    val context = LocalContext.current
    LaunchedEffect(imageUrl) {
        imageUrl?.let {
            val request = ImageRequest.Builder(context)
                .data(it)
                .allowHardware(false)
                .build()

            val drawable = Coil.imageLoader(context).execute(request).drawable
            val bitmap = (drawable as? BitmapDrawable)?.bitmap
            bitmap?.let { bmp ->
                Palette.from(bmp).generate { palette ->
                    dominantColor = Color(
                        palette?.getDominantColor(Color.White.toArgb())
                            ?: Color.White.toArgb()
                    )
                }
            }
        }
    }
    return dominantColor
}

fun grayscaleFilter(saturation: Float = 0f): ColorFilter {
    val matrix = ColorMatrix().apply { setToSaturation(saturation) }
    return ColorFilter.colorMatrix(matrix)
}

fun darkenColorFilter(darkness: Float): ColorFilter {
    val alpha = darkness.coerceIn(0f, 1f)
    val scale = 1f - alpha

    // 1. Create the Saturation Matrix
    val matrix = ColorMatrix().apply {
        setToSaturation(scale)
    }

    // 2. Create the Scaling (Brightness) Matrix
    val scaleMatrix = ColorMatrix(
        floatArrayOf(
            scale, 0f, 0f, 0f, 0f,
            0f, scale, 0f, 0f, 0f,
            0f, 0f, scale, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )

    // 3. Combine them using the multiplication operator
    // This is the Compose equivalent of postConcat
    matrix *= scaleMatrix

    return ColorFilter.colorMatrix(matrix)
}