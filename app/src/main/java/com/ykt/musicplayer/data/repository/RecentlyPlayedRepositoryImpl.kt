package com.ykt.musicplayer.data.repository

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.snapshotFlow
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.domain.repository.BaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class RecentlyPlayedRepositoryImpl(
    private val firestore: FirebaseFirestore
): BaseRepository<Song> {
    private val recentlyPlayed = firestore.collection("recently_played")
    private val songs = firestore.collection("songs")

    fun addRecentlyPlayed(songId: String) {
        val query = recentlyPlayed.whereEqualTo("songId", songId)
        query.get().addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                val doc = snapshot.documents.first()
                doc.reference.update("playedAt", FieldValue.serverTimestamp())
            } else {
                val data = hashMapOf(
                    "songId" to songId,
                    "playedAt" to FieldValue.serverTimestamp(),

                )
                recentlyPlayed.add(data)
            }
        }
    }

    override fun getItems(): Flow<List<Song>> = callbackFlow {
        val listener = recentlyPlayed
            .orderBy("playedAt", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val songIds = snapshot?.documents?.mapNotNull {
                    it.getString("songId")
                } ?: emptyList()

                if (songIds.isEmpty()) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                songs.whereIn("id", songIds).get()
                    .addOnSuccessListener { songSnapshot ->
                        val songsList = songSnapshot.documents.mapNotNull {
                            it.toObject(Song::class.java)
                        }
                        val ordered = songIds.mapNotNull { id ->
                            songsList.find {
                                it.id == id
                            }
                        }
                        trySend(ordered)
                    }
                    .addOnFailureListener { e ->
                        close(e)
                    }
            }
        awaitClose { listener.remove() }
    }
}