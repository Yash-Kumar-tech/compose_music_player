package com.ykt.musicplayer.domain.repository

import com.ykt.musicplayer.domain.model.Section
import kotlinx.coroutines.flow.Flow

interface SectionRepository {
    fun getSections(): Flow<List<Section>>
}