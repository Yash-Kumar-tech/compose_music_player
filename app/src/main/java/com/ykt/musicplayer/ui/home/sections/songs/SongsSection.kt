package com.ykt.musicplayer.ui.home.sections.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.ui.home.components.SectionHeader
import com.ykt.musicplayer.ui.home.components.SongGridItem

@Composable
fun SongsSection(
    title: String,
    songs: List<Song>,
    onSongClick: (Song) -> Unit
) {
    Column {
        SectionHeader(title)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(songs) { song ->
//                Log.d("Song Click", song.toString())
                SongGridItem(song = song, onClick = { onSongClick(song) })
            }
        }
    }
}