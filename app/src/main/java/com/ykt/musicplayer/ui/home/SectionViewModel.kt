package com.ykt.musicplayer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ykt.musicplayer.domain.repository.BaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class SectionViewModel<T>(
    repository: BaseRepository<T>
) : ViewModel() {
    val items: StateFlow<List<T>> =
        repository.getItems()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}