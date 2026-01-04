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
            theme = prefs[SettingsKeys.THEME] ?: "system",
            screenTimeoutMs = prefs[SettingsKeys.SCREEN_TIMEOUT] ?: 5000L
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
}