package com.ykt.musicplayer.domain.repository

import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {
    fun getItems(): Flow<List<T>>
}