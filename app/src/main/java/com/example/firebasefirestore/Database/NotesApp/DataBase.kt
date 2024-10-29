package com.example.firebasefirestore.Database.NotesApp

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

// Your existing Notes and NotesSaver implementation remains the same
data class Notes(
    var title: String = "",
    var content: String = ""
)

fun addNote(
    context: Context,
    firestore: FirebaseFirestore,
    name: String,     // User identifier (could be user ID instead of name)
    title: String,    // Title of the note
    content: String,  // Content of the note
    onAddSuccess: (Boolean) -> Unit
) {
    // Create a new note object
    val note = Notes(title, content)

    // Add the note to the Fire store under the specified user's notes collection
    firestore.collection("Note") // Top-level collection
        .document(name)           // Document for the specific user (consider using user ID instead of name)
        .collection("UserNotes")  // Sub-collection for user notes (to avoid collision with titles)
        .document()               // Automatically generate a document ID
        .set(note)                 // Store the note
        .addOnSuccessListener {
            onAddSuccess(true)     // Callback indicating success
            Toast.makeText(context, "Note added successfully", Toast.LENGTH_SHORT).show()
            Log.d("Firestore", "Note added successfully: $note") // Log success with note details
        }
        .addOnFailureListener { e ->
            onAddSuccess(false)     // Callback indicating failure
            Toast.makeText(context, "Error adding note: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("Firestore", "Error adding note", e) // Log the error
        }
}

fun fetchAllNotes(context: Context, firestore: FirebaseFirestore,name: String, onFetchSuccess: (List<Notes>) -> Unit) {
    firestore.collection("Note").document(name).collection("UserNotes").get()
        .addOnSuccessListener { result ->
            // Create a mutable list to hold the fetched notes
            val notesList = mutableListOf<Notes>()

            // Loop through the documents in the result
            for (document in result.documents) {
                // Extract the note data and create a Notes object
                val note = document.toObject(Notes::class.java)
                if (note != null) {
                    notesList.add(note) // Add the note to the list
                }
            }

            // Notify success and pass the list of notes
            Toast.makeText(context, "Notes fetched successfully", Toast.LENGTH_SHORT).show()
            onFetchSuccess(notesList)
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error fetching notes", Toast.LENGTH_SHORT).show()
            Log.e("FireStore", "Error fetching notes", e)
        }
}

fun fetchNotesWithTitle(
    context: Context,
    name: String,
    firestore: FirebaseFirestore,
    title: String,
    onFetchSuccess: (Notes?, String?) -> Unit // Change to accept a single Notes object
) {
    firestore.collection("Note").document(name).collection("UserNotes")
        .whereEqualTo("title", title)  // Query where title equals the provided value
        .limit(1) // Limit to only one result
        .get()
        .addOnSuccessListener { result ->
            if (result.isEmpty) {
                Toast.makeText(context, "No notes found", Toast.LENGTH_SHORT).show()
                onFetchSuccess(null,null) // Return null if no note found
                return@addOnSuccessListener
            }

            val document = result.documents.firstOrNull()
            val note = document?.toObject(Notes::class.java)
            val documentId = document?.id // Retrieve document ID

            if (note != null) {
                Toast.makeText(context, "Note fetched successfully", Toast.LENGTH_SHORT).show()
                onFetchSuccess(note, documentId) // Pass the single note object
            } else {
                Toast.makeText(context, "Error converting note", Toast.LENGTH_SHORT).show()
                onFetchSuccess(null, null) // Return null if conversion fails
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error fetching notes", Toast.LENGTH_SHORT).show()
            Log.e("FireStore", "Error fetching notes", e)
            onFetchSuccess(null, null) // Return null in case of error
        }
}