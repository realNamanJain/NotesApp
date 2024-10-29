package com.example.firebasefirestore.Database.MVVM

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


// Your existing Notes and NotesSaver implementation remains the same
data class Notes(
    var title: String = "",
    var content: String = ""
)

class NotesRepository(private val firestore: FirebaseFirestore) {

    suspend fun addNote(
        name: String,
        note: Notes
    ): Boolean {
        return try {
            firestore.collection("Note")
                .document(name)
                .collection("UserNotes")
                .document()
                .set(note)
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding note", e)
            false
        }
    }

    suspend fun fetchAllNotes(name: String): List<Notes> {
        return try {
            firestore.collection("Note").document(name).collection("UserNotes")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Notes::class.java) }
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching notes", e)
            emptyList()
        }
    }

    suspend fun fetchNotesWithTitle(name: String, title: String): Pair<Notes?, String?> {
        return try {
            val result = firestore.collection("Note").document(name).collection("UserNotes")
                .whereEqualTo("title", title)
                .limit(1)
                .get()
                .await()
            val document = result.documents.firstOrNull()
            Pair(document?.toObject(Notes::class.java), document?.id)
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching note by title", e)
            Pair(null, null)
        }
    }

    suspend fun updateNote(
        name: String,
        documentId: String,
        note: Notes
    ): Boolean {
        return try {
            firestore.collection("Note").document(name)
                .collection("UserNotes").document(documentId)
                .set(note)
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating note", e)
            false
        }
    }
}
