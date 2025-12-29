package com.ykt.musicplayer.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ykt.musicplayer.domain.model.Song
import com.ykt.musicplayer.domain.repository.BaseRepository
import com.ykt.musicplayer.domain.repository.SongRepository
import io.appwrite.services.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val storage: Storage
) : BaseRepository<Song> {
    override fun getItems(): Flow<List<Song>> = callbackFlow {
        val listener = firebaseFirestore.collection("songs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val songs = snapshot?.toObjects(Song::class.java) ?: emptyList<Song>()
                trySend(songs)
            }
        awaitClose { listener.remove() }
    }

    suspend fun downloadFile(bucketId: String, fileId: String): ByteArray {
        val response = storage.getFileDownload(bucketId, fileId)
        return response
    }
}