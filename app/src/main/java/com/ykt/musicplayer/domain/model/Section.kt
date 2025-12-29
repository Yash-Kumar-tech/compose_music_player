package com.ykt.musicplayer.domain.model

data class Section(
    val id: String = "",
    val title: String = "",
    val order: Int = 0,
    val type: String = "",
    val collection: String = "",
)
