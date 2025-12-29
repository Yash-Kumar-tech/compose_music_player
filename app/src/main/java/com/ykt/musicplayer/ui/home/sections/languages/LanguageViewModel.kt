package com.ykt.musicplayer.ui.home.sections.languages

import com.ykt.musicplayer.data.repository.LanguageRepositoryImpl
import com.ykt.musicplayer.data.repository.SongRepositoryImpl
import com.ykt.musicplayer.domain.model.Language
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.ui.home.SectionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    repo: LanguageRepositoryImpl
): SectionViewModel<Language>(repo) {
}