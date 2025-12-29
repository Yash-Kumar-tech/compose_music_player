package com.ykt.musicplayer.data.playback

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavHostController
import coil.Coil
import coil.request.ImageRequest
import com.ykt.musicplayer.MainActivity
import com.ykt.musicplayer.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationManagerProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @OptIn(UnstableApi::class)
    fun create(service: Service): PlayerNotificationManager {
        return PlayerNotificationManager.Builder(
            context,
            1,
            "music_channel"
        )
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    val meta = player.currentMediaItem?.mediaMetadata
                    return meta?.title ?: "Unknown"
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val currentId = player.currentMediaItem?.mediaId ?: ""
                    val intent = Intent(
                        context,
                        MainActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("deeplink_song_id", currentId)
                    }

                    return PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    val meta = player.currentMediaItem?.mediaMetadata
                    return meta?.artist
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    val meta = player.currentMediaItem?.mediaMetadata
                    val url = meta?.artworkUri ?: return null

                    val request = ImageRequest.Builder(context)
                        .data(url)
                        .allowHardware(false)
                        .target { drawable ->
                            val bitmap = (drawable as? BitmapDrawable)?.bitmap
                            if (bitmap != null) {
                                callback.onBitmap(bitmap)
                            }
                        }
                        .build()

                    Coil.imageLoader(context).enqueue(request)
                    return null
                }
            })
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(id: Int, notification: Notification, ongoing: Boolean) {
                    service.startForeground(id, notification)
                }

                override fun onNotificationCancelled(id: Int, dismissedByUser: Boolean) {
                    service.stopForeground(true)
                    service.stopSelf()
                }
            })
            .build()
    }
}