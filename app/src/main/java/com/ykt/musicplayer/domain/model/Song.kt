package com.ykt.musicplayer.domain.model

import androidx.media3.common.MediaItem
import com.google.firebase.Timestamp

data class Song(
    val id: String = "",
    val title: String = "",
    val artist: String = "",
    val audioUrl: String = "",
    val thumbnailUrl: String = "",
    val videoUrl: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val genre: String = "",
    val keywords: List<String> = emptyList(),
)

fun MediaItem.toSong(): Song {
    val meta = mediaMetadata
    return Song(
        id = mediaId, // or use custom tag if you set one
        title = meta.title?.toString().orEmpty(),
        artist = meta.artist?.toString().orEmpty(),
        audioUrl = localConfiguration?.uri?.toString().orEmpty(),
        thumbnailUrl = meta.artworkUri?.toString().orEmpty()
    )
}
