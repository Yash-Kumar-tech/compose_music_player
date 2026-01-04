package com.ykt.musicplayer.ui.player.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ykt.musicplayer.domain.model.Playlist
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSheet(
    playlists: List<Playlist>,
    hazeState: HazeState,
    dominantColor: Color,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onPlaylistSelect: (Playlist) -> Unit,
    onCreateNewClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = null
    ) {
        FrostedPanel(
            hazeState = hazeState,
            radius = 24.dp,
            tint = dominantColor.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Add to Playlist",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn {
                    item {
                        ListItem(
                            headlineContent = { Text("Create New Playlist") },
                            leadingContent = { Icon(Icons.Rounded.Add, contentDescription = null) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier.clickable {
                                onCreateNewClick()
                            }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    }
                    items(playlists) { playlist ->
                        ListItem(
                            headlineContent = { Text(playlist.name) },
                            supportingContent = { Text("${playlist.songIds.size} songs") },
                            leadingContent = { Icon(Icons.Rounded.PlaylistPlay, contentDescription = null) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier.clickable {
                                onPlaylistSelect(playlist)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    hazeState: HazeState,
    dominantColor: Color,
    onDismissRequest: () -> Unit,
    onCreateClick: (String) -> Unit
) {
    var newPlaylistName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        FrostedPanel(
            hazeState = hazeState,
            radius = 28.dp,
            tint = dominantColor.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "New Playlist",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )

                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist Name", color = Color.White.copy(alpha = 0.7f)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel", color = Color.White.copy(alpha = 0.8f))
                    }
                    TextButton(
                        onClick = {
                            if (newPlaylistName.isNotBlank()) {
                                onCreateClick(newPlaylistName)
                                newPlaylistName = ""
                            }
                        }
                    ) {
                        Text("Create", color = Color.White)
                    }
                }
            }
        }
    }
}
