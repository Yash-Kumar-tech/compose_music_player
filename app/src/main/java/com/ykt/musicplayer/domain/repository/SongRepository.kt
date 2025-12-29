package com.ykt.musicplayer.domain.repository

import com.ykt.musicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getSongs(): Flow<List<Song>>
}