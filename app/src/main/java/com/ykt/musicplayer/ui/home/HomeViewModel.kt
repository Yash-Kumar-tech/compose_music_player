package com.ykt.musicplayer.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ykt.musicplayer.data.repository.RecentlyPlayedRepositoryImpl
import com.ykt.musicplayer.domain.model.Section
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.domain.repository.SectionRepository
import com.ykt.musicplayer.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sectionRepository: SectionRepository,
//    private val songsRepository: SongRepository,
) : ViewModel() {

    val sections: StateFlow<List<Section>> =
        sectionRepository.getSections()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                emptyList()
            )
//    val songs: StateFlow<List<Song>> = songsRepository.getSongs()
//        .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(),
//            emptyList()
//        )
}