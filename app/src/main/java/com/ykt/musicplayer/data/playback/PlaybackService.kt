package com.ykt.musicplayer.data.playback

import android.R
import android.app.Service
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import coil.Coil
import coil.request.ImageRequest

@UnstableApi
@AndroidEntryPoint
class PlaybackService: Service() {
    @Inject lateinit var audioPlayer: ExoPlayer
    @Inject lateinit var notificationProvider: NotificationManagerProvider
    @Inject lateinit var mediaSessionManager: MediaSessionManager

    private lateinit var notificationManager: PlayerNotificationManager
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()

        mediaSession = mediaSessionManager.attachPlayer(audioPlayer)

        audioPlayer.addListener(object: Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState()
            }
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                updatePlaybackState()
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                updatePlaybackState()
                if (playbackState == Player.STATE_ENDED) stopSelf()
            }

        })

        notificationManager = notificationProvider.create(
            service = this,
        ).apply {
            setPlayer(audioPlayer)
            setMediaSessionToken(mediaSession.platformToken)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Service is sticky so playback continues if app is killed
        handleCommand(intent)
        return START_STICKY
    }

    private fun handleCommand(intent: Intent?) {
        when (intent?.action) {
            Actions.PLAY -> {
                val audioUrl = intent.getStringExtra(Extras.AUDIO_URL) ?: return
//                Log.d("PlaybackService", "PLAY url=$audioUrl")
                val title = intent.getStringExtra(Extras.TITLE) ?: ""
                val id = intent.getStringExtra(Extras.ID) ?: ""
                val artist = intent.getStringExtra(Extras.ARTIST) ?: ""
                val artwork = intent.getStringExtra(Extras.ARTWORK_URI)

                val metadata = MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .apply { if (!artwork.isNullOrEmpty()) setArtworkUri(artwork.toUri()) }
                    .build()

                val item = MediaItem.Builder()
                    .setUri(audioUrl)
                    .setMediaId(id)
                    .setMediaMetadata(metadata)
                    .build()

                audioPlayer.setMediaItem(item)
                audioPlayer.prepare()
                audioPlayer.play()

            }
            Actions.PAUSE -> {
                audioPlayer.pause()
            }
            Actions.RESUME -> {
                audioPlayer.play()
            }
            Actions.SEEK_TO -> {
                val pos = intent.getLongExtra(Extras.POSITION_MS, 0L)
                audioPlayer.seekTo(pos)
            }
            Actions.SET_LOOPING -> {
                val looping = intent.getBooleanExtra(Extras.LOOPING, false)
                audioPlayer.repeatMode =
                    if (looping) Player.REPEAT_MODE_ONE
                    else Player.REPEAT_MODE_OFF
            }
            Actions.SET_AUTOPLAY -> {
                val autoplay = intent.getBooleanExtra(Extras.AUTOPLAY, false)
                audioPlayer.playWhenReady = autoplay
            }
        }
    }

    private fun updatePlaybackState() {
        val isPlaying = audioPlayer.isPlaying
        val state =
            if (isPlaying) PlaybackStateCompat.STATE_PLAYING
            else PlaybackStateCompat.STATE_PAUSED

        val playbackState = PlaybackStateCompat.Builder()
            .setState(
                state,
                audioPlayer.currentPosition,
                if (isPlaying) 1.0f else 0.0f
            )
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SEEK_TO or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.setPlayer(null)
        mediaSessionManager.release()
        audioPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

object Actions {
    const val PLAY = "com.ykt.musicplayer.action.PLAY"
    const val PAUSE = "com.ykt.musicplayer.action.PAUSE"
    const val RESUME = "com.ykt.musicplayer.action.RESUME"
    const val SEEK_TO = "com.ykt.musicplayer.action.SEEK_TO"
    const val SET_LOOPING = "com.ykt.musicplayer.action.SET_LOOPING"
    const val SET_AUTOPLAY = "com.ykt.musicplayer.action.SET_AUTOPLAY"
}
object Extras {
    const val AUDIO_URL = "audio_url"
    const val TITLE = "title"
    const val ID = "id"
    const val ARTIST = "artist"
    const val ARTWORK_URI = "artwork_uri"
    const val POSITION_MS = "position_ms"
    const val LOOPING = "looping"
    const val AUTOPLAY = "autoplay"
}
