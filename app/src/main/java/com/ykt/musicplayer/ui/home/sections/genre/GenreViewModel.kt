package com.ykt.musicplayer.ui.home.sections.genre

import com.ykt.musicplayer.data.repository.GenreRepositoryImpl
import com.ykt.musicplayer.data.repository.SongRepositoryImpl
import com.ykt.musicplayer.domain.model.Genre
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.ui.home.SectionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(
    repo: GenreRepositoryImpl
): SectionViewModel<Genre>(repo) {
}