package com.example.firebasefirestore.Database.MemoMate

import android.util.Log
import com.example.firebasefirestore.Database.NotesApp.Notes
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Data class representing a note
data class MemoMate(
    var id: String? = null, // Unique identifier for the note
    var title: String = "", // Title of the note
    var content: String = "" // Content of the note
)

// Repository for managing notes in Firestore
class MemoMateRepository(private val firestore: FirebaseFirestore) {

    // Add a new note to Firestore
    suspend fun addNote(name: String, note: MemoMate): Boolean {
        return try {
            // Store the note under the user's document
            firestore.collection("Note")
                .document(name) // User document
                .collection("UserNotes") // User's notes collection
                .document() // Generate a new document ID
                .set(note) // Set the note data
                .await() // Await completion
            Log.d("MemoMateRepository", "Note added successfully")
            true
        } catch (e: Exception) {
            Log.e("Fire store", "Error adding note", e)
            false // Return false if there was an error
        }
    }

    // Fetch all notes for a specific user
    fun fetchAllNotes(name: String, onResult: (List<MemoMate>) -> Unit) {
        firestore.collection("Note").document(name).collection("UserNotes")
            .orderBy("title") // Order notes by title
            .addSnapshotListener { snapshot, exception -> // Listen for real-time updates
                if (exception != null) {
                    Log.e("Fire store", "Error fetching notes", exception)
                    onResult(emptyList()) // Return an empty list on error
                    return@addSnapshotListener
                }
                // Map Firestore documents to MemoMate objects
                val notesList = snapshot?.documents?.mapNotNull { it.toObject(MemoMate::class.java) } ?: emptyList()
                onResult(notesList) // Pass the list back to the caller
            }
    }

    // Fetch a note by its title
    suspend fun fetchNotesWithTitle(userName: String, title: String): MemoMate? {
        return try {
            val snapshot = firestore.collection("Note")
                .document(userName)
                .collection("UserNotes")
                .whereEqualTo("title", title) // Filter by title
                .get()
                .await() // Await the result

            if (snapshot.isEmpty) {
                Log.d("MemoMateRepository", "No notes found with title: $title")
                return null // No documents found
            }

            // Get the first document and convert it to a MemoMate object
            val document = snapshot.documents.firstOrNull()
            val note = document?.toObject(MemoMate::class.java)

            // Assign the document ID to the MemoMate object
            document?.id?.let {
                Log.e("MemoMate", "Fetched Document ID: $it")
                note?.id = it
            } ?: Log.d("MemoMate", "Document ID is null")

            note // Return the MemoMate object
        } catch (e: Exception) {
            Log.e("MemoMateRepository", "Error fetching note with title $title: ${e.message}")
            null // Return null on failure
        }
    }

    // Update a note's content by its document ID
    suspend fun updateNote(userName: String, documentId: String, updatedContent: String): Boolean {
        return try {
            // Update the note's content in Firestore
            firestore.collection("Note")
                .document(userName)
                .collection("UserNotes")
                .document(documentId)
                .update("content", updatedContent) // Update the content field
                .await() // Await completion

            Log.d("MemoMateRepository", "Note with ID: $documentId updated successfully")
            true // Update successful
        } catch (e: Exception) {
            Log.e("MemoMateRepository", "Error updating note with ID $documentId: ${e.message}")
            false // Return false on failure
        }
    }
}