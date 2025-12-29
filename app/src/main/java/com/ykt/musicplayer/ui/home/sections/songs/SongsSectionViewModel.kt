package com.ykt.musicplayer.ui.home.sections.songs

import com.ykt.musicplayer.data.repository.SongRepositoryImpl
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.ui.home.SectionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongsSectionViewModel @Inject constructor(
    repo: SongRepositoryImpl
): SectionViewModel<Song>(repo) {
}