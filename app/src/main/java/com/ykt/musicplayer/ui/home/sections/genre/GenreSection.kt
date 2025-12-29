package com.ykt.musicplayer.ui.home.sections.genre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.ykt.musicplayer.domain.model.Genre
import com.ykt.musicplayer.ui.home.components.SectionHeader

@Composable
fun GenreSection(
    title: String,
    genres: List<Genre>,
    onGenreClick: (Genre) -> Unit
) {
    Column {
        SectionHeader(title)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(genres) { genre ->
                AssistChip(
                    onClick = { onGenreClick(genre) },
                    label = { Text(genre.name) }
                )
            }
        }

    }
}