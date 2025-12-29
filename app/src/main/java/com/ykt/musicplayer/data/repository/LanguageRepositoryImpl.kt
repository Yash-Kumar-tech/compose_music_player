package com.ykt.musicplayer.data.repository

import androidx.compose.runtime.snapshotFlow
import com.google.firebase.firestore.FirebaseFirestore
import com.ykt.musicplayer.domain.model.Genre
import com.ykt.musicplayer.domain.model.Language
import com.ykt.musicplayer.domain.repository.BaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): BaseRepository<Language> {
    private val genresCollection = firestore.collection("genres")

    override fun getItems(): Flow<List<Language>> = callbackFlow {
        val listener = genresCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val languages = snapshot?.toObjects(Language::class.java) ?: emptyList()
            trySend(languages)
        }
        awaitClose { listener.remove() }
    }
}