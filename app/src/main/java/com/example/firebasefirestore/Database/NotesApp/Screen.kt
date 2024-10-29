package com.example.firebasefirestore.Database.NotesApp

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun MemoMateScreen(firestore: FirebaseFirestore, navController: NavHostController) {
    val name = getCurrentUserName()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "MemoMate", style = TextStyle(fontSize = 24.sp), modifier = Modifier.padding(top = 16.dp))
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.navigate("memo_UI") }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "+ Add your memo")
        }
        name?.let { UserNotesScreen(navController, firestore, it) }
    }
}

//this function shows ths the UI of the notes screen where you can write the notes
@Composable
fun MemoUI(navController: NavHostController,firestore: FirebaseFirestore) {
    val context = LocalContext.current
    val name = getCurrentUserName()
    var noteTitle by rememberSaveable { mutableStateOf("") }
    var noteContent by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Note Details", style = TextStyle(fontSize = 24.sp), modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(
                onClick = {
                    name?.let {
                        addNote(context, firestore, it, noteTitle, noteContent) { success ->
                            val message = if (success) "Note added successfully" else "Error in adding note"
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    navController.navigate("memoMate_screen")
                }
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save Note", tint = Color.Black)
            }
        }

        Text(text = "Title", style = TextStyle(fontSize = 16.sp))
        TextField(value = noteTitle, onValueChange = { noteTitle = it }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), singleLine = true)

        Text(text = "Content", style = TextStyle(fontSize = 16.sp))
        TextField(
            value = noteContent,
            onValueChange = { noteContent = it },
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFEEEEEE),
                unfocusedContainerColor = Color(0xFFEEEEEE)
            )
        )
    }
}

//this function shows all notes present in the user database
@Composable
fun UserNotesScreen(navController: NavHostController, firestore: FirebaseFirestore, userId: String) {
    val context = LocalContext.current
    var notes by rememberSaveable { mutableStateOf<List<Notes>>(emptyList()) }

    LaunchedEffect(userId) {
        fetchAllNotes(context, firestore, userId) { fetchedNotes ->
            notes = fetchedNotes
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        LazyColumn {
            items(notes) { note ->
                NoteCard(navController = navController, note = note)
            }
        }
    }
}

@Composable
fun MemoDetailScreen(navController: NavHostController, firestore: FirebaseFirestore, title: String) {
    val context = LocalContext.current
    val name = getCurrentUserName()

    var note by rememberSaveable { mutableStateOf<Notes?>(null) }
    var documentId by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) } // State for loading

    // Fetch the note based on title when the Composable is first composed
    LaunchedEffect(title, name) { // Will run only once per title and name
        if (name != null) {
            fetchNotesWithTitle(context, name, firestore, title) { fetchedNote, id ->
                note = fetchedNote
                documentId = id // Store the document ID
                isLoading = false // Mark loading as complete
            }
        }
    }

    // Show loading state
    if (isLoading) {
        Text(text = "Loading note...", style = TextStyle(fontSize = 16.sp))
        return
    }

    // Display the note details if the note has been fetched
    note?.let { currentNote ->
        // Use state for note title and content
        var noteTitle by rememberSaveable { mutableStateOf(currentNote.title) }
        var noteContent by rememberSaveable { mutableStateOf(currentNote.content) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Note Details",
                    style = TextStyle(fontSize = 24.sp),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                IconButton(
                    onClick = {
                        val noteToSave = Notes(title = noteTitle, content = noteContent)
                        if (name != null) {
                            // Check if we have a document ID to update the existing note
                            if (documentId != null) {
                                Log.d("MemoDetailScreen", "Document ID: $documentId") // Logging for debugging
                                firestore.collection("Note").document(name)
                                    .collection("UserNotes").document(documentId!!)
                                    .set(noteToSave)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Note updated successfully", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Error updating note", Toast.LENGTH_SHORT).show()
                                        Log.e("Firestore", "Error updating note", e)
                                    }
                            } else {
                                // If no document ID, add a new note
                                addNote(context, firestore, name, noteTitle, noteContent) { success ->
                                    if (success) {
                                        Toast.makeText(context, "Note added successfully", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Error adding note", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save Note",
                        tint = Color.Black
                    )
                }
            }

            // Title TextField
            Text(text = "Title", style = TextStyle(fontSize = 16.sp))
            TextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp)
            )

            // Content TextField
            Text(text = "Content", style = TextStyle(fontSize = 16.sp))
            TextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEEEEEE),
                    unfocusedContainerColor = Color(0xFFEEEEEE)
                )
            )
        }
    } ?: run {
        // Handle the case where the note is still null
        Text(text = "Note not found", style = TextStyle(fontSize = 16.sp))
    }
}


@Composable
fun NoteCard(navController: NavHostController, note: Notes) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Navigate to the detail screen with the note title as an argument
                navController.navigate("memo_detailScreen/${note.title}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = note.title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun previeew(){
//    MemoMateScreen()
}
