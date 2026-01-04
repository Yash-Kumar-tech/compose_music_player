package com.ykt.musicplayer.domain.repository

import com.ykt.musicplayer.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(name: String)
    suspend fun addSongToPlaylist(playlistId: String, songId: String)
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String)
    suspend fun deletePlaylist(playlistId: String)
}
