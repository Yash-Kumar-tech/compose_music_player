import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ykt.musicplayer.domain.model.Playlist
import com.ykt.musicplayer.ui.home.components.SectionHeader
import com.ykt.musicplayer.ui.home.sections.playlists.PlaylistGridItem

@Composable
fun PlaylistsSection(
    title: String,
    playlists: List<Playlist>,
    isExpanded: Boolean,
    onPlaylistClick: (Playlist) -> Unit,
    onCreateClick: () -> Unit,
    onShowAllClick: () -> Unit
) {
    val displayedPlaylists = if (isExpanded) playlists else playlists.take(10)
    // totalItems includes the "New Playlist" card
    val totalItems = displayedPlaylists.size + 1
    val rowCount = if (totalItems <= 1) 1 else if (totalItems == 2) 2 else 2 // Wait, if 2 items, can do 1x2 or 2x1. Grid is horizontal.
    // If we have 2 items total (New + 1 playlist), it will be 1 column of 2 rows.
    val actualRows = if (totalItems <= 1) 1 else 2
    val gridHeight = if (actualRows == 1) 170.dp else 340.dp

    Column {
        SectionHeader(title, onShowAllClick = onShowAllClick)
        LazyHorizontalGrid(
            rows = GridCells.Fixed(actualRows),
            modifier = Modifier.height(gridHeight),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { onCreateClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "New Playlist",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                }
            }
            items(displayedPlaylists) { playlist ->
                PlaylistGridItem(
                    playlist = playlist,
                    onClick = { onPlaylistClick(playlist) }
                )
            }
        }
    }
}
