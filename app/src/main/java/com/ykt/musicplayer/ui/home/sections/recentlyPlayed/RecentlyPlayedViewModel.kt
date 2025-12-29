package com.ykt.musicplayer.ui.home.sections.recentlyPlayed

import com.ykt.musicplayer.data.repository.RecentlyPlayedRepositoryImpl
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.ui.home.SectionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecentlyPlayedViewModel @Inject constructor(
    repo: RecentlyPlayedRepositoryImpl
): SectionViewModel<Song>(repo) {

}