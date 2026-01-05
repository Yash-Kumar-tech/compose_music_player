package com.ykt.musicplayer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.ykt.musicplayer.domain.model.AppSettings
import com.ykt.musicplayer.utils.SettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    val settingsFlow: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            looping = prefs[SettingsKeys.LOOPING] ?: false,
            autoplay = prefs[SettingsKeys.AUTOPLAY] ?: false,
            shuffle = prefs[SettingsKeys.SHUFFLE] ?: false,
            theme = prefs[SettingsKeys.THEME] ?: "system",
            screenTimeoutMs = prefs[SettingsKeys.SCREEN_TIMEOUT] ?: 5000L,
            audioQuality = prefs[SettingsKeys.AUDIO_QUALITY] ?: "normal",
            dynamicColors = prefs[SettingsKeys.DYNAMIC_COLORS] ?: true,
            showLyrics = prefs[SettingsKeys.SHOW_LYRICS] ?: true,
        )
    }

    suspend fun updateLooping(looping: Boolean) {
        dataStore.edit {
            it[SettingsKeys.LOOPING] = looping
        }
    }

    suspend fun updateAutoPlay(autoPlay: Boolean) {
        dataStore.edit {
            it[SettingsKeys.AUTOPLAY] = autoPlay
        }
    }

    suspend fun updateShuffle(shuffle: Boolean) {
        dataStore.edit {
            it[SettingsKeys.SHUFFLE] = shuffle
        }
    }

    suspend fun updateTheme(theme: String) {
        dataStore.edit {
            it[SettingsKeys.THEME] = theme
        }
    }

    suspend fun updateScreenTimeoutMs(timeoutMs: Long) {
        dataStore.edit {
            it[SettingsKeys.SCREEN_TIMEOUT] = timeoutMs
        }
    }

    suspend fun updateAudioQuality(quality: String) {
        dataStore.edit {
            it[SettingsKeys.AUDIO_QUALITY] = quality
        }
    }

    suspend fun updateDynamicColors(enabled: Boolean) {
        dataStore.edit {
            it[SettingsKeys.DYNAMIC_COLORS] = enabled
        }
    }

    suspend fun updateShowLyrics(enabled: Boolean) {
        dataStore.edit {
            it[SettingsKeys.SHOW_LYRICS] = enabled
        }
    }
}