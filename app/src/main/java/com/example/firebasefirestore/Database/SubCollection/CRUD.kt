package com.example.firebasefirestore.Database.SubCollection

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

data class Notes(
    val title: String = "",
    val content: String = ""
)
fun addNotes(firestore: FirebaseFirestore){
    val note = Notes("My First Note", "This is my first note in FireStore!")
    saveNote(firestore, "Note_1", note)
}

fun saveNote(firestore: FirebaseFirestore,documentId: String, note: Notes){
    firestore.collection("Notes").document(documentId).set(note)
        .addOnSuccessListener {
            Log.d("FireStore", "Note added successfully")
        }
        .addOnFailureListener { e ->
            Log.e("FireStore", "Error adding note", e)
        }
}

fun fetchNote(firestore: FirebaseFirestore, onComplete: (Notes?) -> Unit){
    firestore.collection("Notes").document("Note_1").get()
        .addOnSuccessListener {documentSnapshot ->
            val note = documentSnapshot.toObject(Notes::class.java)
            onComplete(note)
        }
        .addOnFailureListener { e ->
            Log.e("FireStore", "Error fetching note", e)
            onComplete(null)
        }
}

fun updateNote(firestore: FirebaseFirestore,updatedString: String, onComplete: (Boolean) -> Unit){
//    firestore.collection("Notes").document("Note_1").update("content",updatedString)
//        .addOnSuccessListener {
//            Log.d("FireStore", "Note updated successfully")
//        }
//        .addOnFailureListener { e ->
//            Log.e("FireStore", "Error updating note", e)
//        }
    val documentRef = firestore.collection("Notes").document("Note_1")

    // Fetch the current content from Firestore
    documentRef.get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val currentNote = documentSnapshot.toObject(Notes::class.java)

                if (currentNote != null) {
                    // Merge the new content with the existing content
                    val mergedContent = currentNote.content + " " + updatedString

                    // Update the Fire store document with the merged content
                    documentRef.update("content", mergedContent)
                        .addOnSuccessListener {
                            Log.d("FireStore", "Note updated successfully with merged content")
                            onComplete(true) // Success
                        }
                        .addOnFailureListener { e ->
                            Log.e("FireStore", "Error updating note", e)
                            onComplete(false) // Failure
                        }
                } else {
                    Log.e("FireStore", "Error: current note is null")
                    onComplete(false)
                }
            } else {
                Log.e("FireStore", "Error: document does not exist")
                onComplete(false)
            }
        }
        .addOnFailureListener { e ->
            Log.e("FireStore", "Error fetching note", e)
            onComplete(false)
        }
}

@Composable
fun DisplayNotes(firestore: FirebaseFirestore) {
    val context = LocalContext.current
    var note by remember { mutableStateOf(Notes()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch the note when the composable is launched
    LaunchedEffect(Unit) {
        fetchNote(firestore) { fetchedNote ->
            note = fetchedNote ?: Notes()  // If null, use an empty note
            isLoading = false
        }
    }

    // Display loading indicator or the note
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Title: ${note.title}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Content: ${note.content}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))

            // Button to update the note
            Button(onClick = {
                Toast.makeText(context, "Updating note...", Toast.LENGTH_SHORT).show()
                isLoading = true
                updateNote(firestore, "This is an updated note and there are more changes") { success ->
                    Toast.makeText(context, "Note updated: $success", Toast.LENGTH_SHORT).show()
                    if (success) {
                        // Re-fetch the updated note after updating
                        fetchNote(firestore) { updatedNote ->
                            note = updatedNote ?: Notes()
                            isLoading = false
                        }
                    } else {
                        isLoading = false
                    }
                }
            }) {
                Text("Update Note")
            }
        }
    }
}
