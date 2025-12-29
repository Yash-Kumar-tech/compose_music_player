package com.ykt.musicplayer.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ykt.musicplayer.data.repository.SettingsRepository
import com.ykt.musicplayer.domain.model.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {
    val settings: StateFlow<AppSettings> =
        repo.settingsFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            AppSettings()
        )

    fun toggleLooping() = viewModelScope.launch {
        repo.updateLooping(!settings.value.looping)
    }

    fun toggleAutoPlay() = viewModelScope.launch {
        repo.updateAutoPlay(!settings.value.autoplay)
    }

    fun setTheme(theme: String) = viewModelScope.launch {
        repo.updateTheme(theme)
    }

}