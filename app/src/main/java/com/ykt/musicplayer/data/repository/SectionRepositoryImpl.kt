package com.ykt.musicplayer.data.repository

import com.ykt.musicplayer.domain.model.Section
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.domain.repository.SectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SectionRepositoryImpl: SectionRepository {

    override fun getSections(): Flow<List<Section>> = flow {
        emit(
            listOf(
                Section("all_songs", "All Songs", 0, "songs", "songs"),
                Section("playlists", "Playlists", 1, "playlists", "playlists"),
                Section("recently_played", "Recently Played", 2, "history", "recently_played"),
                Section("genres", "Genres", 3, "genres", "genres"),
                Section("languages", "Languages", 4, "languages", "languages")
            )
        )
    }
}