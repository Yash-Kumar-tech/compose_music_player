package com.ykt.musicplayer.data.playback

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.google.android.gms.cast.framework.MediaNotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.NonCancellable.isActive
import javax.inject.Inject

class MediaSessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var mediaSession: MediaSession? = null

    fun attachPlayer(player: Player): MediaSession {
        mediaSession = MediaSession.Builder(context, player).build()
        return mediaSession!!
    }

    fun getSession(): MediaSession? = mediaSession

    fun release() {
        mediaSession?.release()
        mediaSession = null
    }
}