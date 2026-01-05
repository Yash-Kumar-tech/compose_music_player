package com.ykt.musicplayer.domain.model

data class AppSettings(
    val looping: Boolean = false,
    val autoplay: Boolean = false,
    val shuffle: Boolean = false,
    val theme: String = "system",
    val screenTimeoutMs: Long = 5000L,
    val audioQuality: String = "normal",
    val dynamicColors: Boolean = true,
    val showLyrics: Boolean = true,
)
