package com.ykt.musicplayer.data.repository

import androidx.compose.runtime.snapshotFlow
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.ykt.musicplayer.domain.model.Genre
import com.ykt.musicplayer.domain.repository.BaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): BaseRepository<Genre> {
    private val genresCollection = firestore.collection("genres")

    override fun getItems(): Flow<List<Genre>> = callbackFlow {
        val listener = genresCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val genres = snapshot?.toObjects(Genre::class.java) ?: emptyList()
            trySend(genres)
        }
        awaitClose { listener.remove() }
    }
}