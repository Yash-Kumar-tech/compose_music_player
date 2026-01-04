package com.ykt.musicplayer.ui.home.sections.recentlyPlayed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.ui.Modifier
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.ui.home.components.SectionHeader
import com.ykt.musicplayer.ui.home.components.SongGridItem

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecentlyPlayedSection(
    title: String,
    sectionId: String,
    songs: List<Song>,
    isExpanded: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSongClick: (Song) -> Unit,
    onShowAllClick: () -> Unit
) {
    val displayedSongs = if (isExpanded) songs else songs.take(10)
    val rowCount = if (displayedSongs.size <= 1) 1 else 2
    val gridHeight = if (rowCount == 1) 190.dp else 380.dp

    Column {
        SectionHeader(title, onShowAllClick = onShowAllClick)
        LazyHorizontalGrid(
            rows = GridCells.Fixed(rowCount),
            modifier = Modifier.height(gridHeight),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(displayedSongs) { song ->
                SongGridItem(
                    song = song,
                    elementKey = "thumbnail_${song.id}_$sectionId",
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onClick = { onSongClick(song) }
                )
            }
        }
    }
}