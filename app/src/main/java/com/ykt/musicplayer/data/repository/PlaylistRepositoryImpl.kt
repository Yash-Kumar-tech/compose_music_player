package com.ykt.musicplayer.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ykt.musicplayer.domain.model.Playlist
import com.ykt.musicplayer.domain.repository.PlaylistRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> = callbackFlow {
        val listener = firestore.collection("playlists")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val playlists = snapshot?.toObjects(Playlist::class.java) ?: emptyList()
                trySend(playlists)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createPlaylist(name: String) {
        val doc = firestore.collection("playlists").document()
        val playlist = Playlist(
            id = doc.id,
            name = name,
            createdAt = System.currentTimeMillis()
        )
        doc.set(playlist).await()
    }

    override suspend fun addSongToPlaylist(playlistId: String, songId: String) {
        firestore.collection("playlists").document(playlistId)
            .update("songIds", FieldValue.arrayUnion(songId))
            .await()
    }

    override suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {
        firestore.collection("playlists").document(playlistId)
            .update("songIds", FieldValue.arrayRemove(songId))
            .await()
    }

    override suspend fun deletePlaylist(playlistId: String) {
        firestore.collection("playlists").document(playlistId)
            .delete()
            .await()
    }
}
