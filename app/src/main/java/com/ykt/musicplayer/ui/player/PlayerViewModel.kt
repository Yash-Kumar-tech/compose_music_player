package com.ykt.musicplayer.ui.player

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.ykt.musicplayer.data.cache.buildCacheDataSourceFactory
import com.ykt.musicplayer.data.playback.MediaSessionManager
import com.ykt.musicplayer.data.playback.NotificationManagerProvider
import com.ykt.musicplayer.data.repository.RecentlyPlayedRepositoryImpl
import com.ykt.musicplayer.data.repository.SettingsRepository
import com.ykt.musicplayer.domain.model.AppSettings
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.utils.NetworkMonitor
import com.ykt.musicplayer.utils.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.ykt.musicplayer.data.playback.Actions
import com.ykt.musicplayer.data.playback.Extras
import com.ykt.musicplayer.data.playback.PlaybackService
import com.ykt.musicplayer.domain.model.toSong
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@UnstableApi
@HiltViewModel
class PlayerViewModel @Inject constructor(
    app: Application,
    networkMonitor: NetworkMonitor,
    private val recentlyPlayedRepository: RecentlyPlayedRepositoryImpl,
    private val settingsRepository: SettingsRepository,
    val audioPlayer: ExoPlayer,
) : AndroidViewModel(app) {

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _playerState = MutableStateFlow(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState

    private val _isLooping = MutableStateFlow(false)
    val isLooping: StateFlow<Boolean> = _isLooping

    private val _autoplay = MutableStateFlow(false)
    val autoplay: StateFlow<Boolean> = _autoplay

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs

    val settings: StateFlow<AppSettings> =
        settingsRepository.settingsFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            AppSettings()
        )

    init {
        // Forward settings changes to service
        viewModelScope.launch {
            settings.collect { prefs ->
                sendServiceCommand(
                    action = Actions.SET_LOOPING,
                    extras = bundleOf(Extras.LOOPING to prefs.looping)
                )
                sendServiceCommand(
                    action = Actions.SET_AUTOPLAY,
                    extras = bundleOf(Extras.AUTOPLAY to prefs.autoplay)
                )
            }
        }

        // Listen to player state changes
        audioPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
                _currentSong.value = item?.toSong() // convert metadata → Song
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _playerState.value = when (playbackState) {
                    Player.STATE_IDLE -> PlayerState.Idle
                    Player.STATE_BUFFERING -> PlayerState.Buffering
                    Player.STATE_READY ->
                        if (audioPlayer.playWhenReady) PlayerState.Playing
                        else PlayerState.Paused
                    Player.STATE_ENDED -> PlayerState.Ended
                    else -> PlayerState.Error
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _playerState.value = PlayerState.Error
            }
        })

        audioPlayer.currentMediaItem?.let {
            _currentSong.value = it.toSong()
        }

        // Poll position/duration regularly
        viewModelScope.launch {
            while (isActive) {
                val pos = audioPlayer.currentPosition
                val dur = audioPlayer.duration
                _positionMs.value = pos
                _durationMs.value = if (dur > 0) dur else 0L
                delay(500)
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun playSong(song: Song) {
        val current = _currentSong.value
        if (current?.id == song.id && _playerState.value == PlayerState.Playing) {
            // Already playing this song → just bring UI in sync
            return
        }
        // Otherwise, start new playback
        _currentSong.value = song
        sendServiceCommand(
            action = Actions.PLAY,
            extras = bundleOf(
                Extras.AUDIO_URL to song.audioUrl,
                Extras.ID to song.id,
                Extras.TITLE to song.title,
                Extras.ARTIST to song.artist,
                Extras.ARTWORK_URI to song.thumbnailUrl
            )
        )
        recentlyPlayedRepository.addRecentlyPlayed(song.id)
        _playerState.value = PlayerState.Playing
    }

    fun togglePlayPause() {
        when (_playerState.value) {
            PlayerState.Playing -> {
                sendServiceCommand(Actions.PAUSE)
                _playerState.value = PlayerState.Paused
            }
            PlayerState.Paused, PlayerState.Ended, PlayerState.Idle -> {
                sendServiceCommand(Actions.RESUME)
                _playerState.value = PlayerState.Playing
            }
            else -> {}
        }
    }

    fun seekTo(positionMs: Long) {
        sendServiceCommand(
            Actions.SEEK_TO,
            bundleOf(Extras.POSITION_MS to positionMs)
        )
    }

    fun toggleAutoPlay() {
        val newAutoPlay = !settings.value.autoplay
        viewModelScope.launch { settingsRepository.updateAutoPlay(newAutoPlay) }
    }

    private fun sendServiceCommand(action: String, extras: Bundle? = null) {
        val ctx = getApplication<Application>()
        val intent = Intent(ctx, PlaybackService::class.java).apply {
            this.action = action
            extras?.let { putExtras(it) }
        }
        ContextCompat.startForegroundService(ctx, intent)
    }

    fun loadSongById(songId: String) {
//        val item = audioPlayer.currentMediaItem
//        if (item != null && item.mediaId == songId) {
//            _currentSong.value = item.toSong()
//        }
        // If already playing, just sync
        val current = _currentSong.value
        if (current?.id == songId) return

        audioPlayer.currentMediaItem?.let { item ->
            if (item.mediaId == songId) {
                _currentSong.value = item.toSong()
                return
            }
        }

        // fallback: fetch from repository
        viewModelScope.launch {
//            val song = recentlyPlayedRepository.getSongById(songId)
//            _currentSong.value = song
        }

    }
}