package com.example.firebasefirestore.Database.MemoMate

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun MemoMateScreen(viewModel: MemoMateViewModel, repository: AuthRepository, navController: NavHostController) {
    val name = repository.getCurrentUserName()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "MemoMate", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.navigate("memo_UI") }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "+ Add your memo")
        }

        name?.let {
            LaunchedEffect(it) {
                viewModel.fetchAllNotes(it) // Start fetching notes for the user
            }
            UserNotesScreen(navController = navController, viewModel = viewModel)
        }
    }
}

// UserNotesScreen.kt
@Composable
fun UserNotesScreen(navController: NavHostController, viewModel: MemoMateViewModel) {
    val notes by viewModel.notesList.observeAsState(emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        LazyColumn {
            items(notes) { note ->
                NoteCard(navController = navController, note = note)
            }
        }
    }
}

@Composable
fun MemoDetailScreen(navController: NavHostController, viewModel: MemoMateViewModel, authViewModel: AuthViewModel, title: String) {
    val context = LocalContext.current
    val name = authViewModel.getCurrentUserName()
    val note by viewModel.currentNote.observeAsState() // Observe the current note

    // Use state for note title and content
    var noteTitle by rememberSaveable { mutableStateOf("") }
    var noteContent by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(true) }

    // Fetch the note by title
    LaunchedEffect(title, name) {
        if (name != null) {
            viewModel.fetchNoteWithTitle(name, title)
        }
    }

    // Update title and content when the note changes
    LaunchedEffect(note) {
        note?.let {
            noteTitle = it.title
            noteContent = it.content
            isLoading = false
            viewModel.currentNoteId = it.id // Set currentNoteId directly
        }
    }

    // Loading state
    if (isLoading) {
        Text(text = "Loading note...", style = MaterialTheme.typography.bodyLarge)
        return
    }

    note?.let { currentNote ->
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Note Details", style = MaterialTheme.typography.titleLarge)

                IconButton(onClick = {
                    val documentId = viewModel.currentNoteId // Get the current document ID

                    if (name != null && documentId != null) {
                        viewModel.updateNote(name, documentId, noteContent) { success ->
                            if (success) {
                                Toast.makeText(context, "Note updated successfully", Toast.LENGTH_SHORT).show()
                                Log.d("MemoMate", "Update successful")
                                navController.popBackStack()
                            } else {
                                Log.e("MemoMate", "Update failed")
                                Toast.makeText(context, "Error updating note", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Log.e("MemoMate", "Update failed: Document ID not found")
                        Toast.makeText(context, "Document ID not found", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Save Note")
                }
            }

            Text(text = "Title", style = MaterialTheme.typography.bodyLarge)
            TextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            Text(text = "Content", style = MaterialTheme.typography.bodyLarge)
            TextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
        }
    }
}


@Composable
fun MemoUI(navController: NavHostController, firestore: FirebaseFirestore, repository: AuthRepository, viewModel: MemoMateViewModel) {
    val context = LocalContext.current
    val name = repository.getCurrentUserName() // Get the current user's name
    var noteTitle by rememberSaveable { mutableStateOf("") }
    var noteContent by rememberSaveable { mutableStateOf("") }

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
                    // Check if name is not null and fields are not empty
                    if (name != null && noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                        viewModel.addNote(name, noteTitle, noteContent) { success ->
                            val message = if (success) {
                                // Clear input fields only if note was added successfully
                                noteTitle = ""
                                noteContent = ""
                                "Note added successfully"
                            } else {
                                "Error in adding note"
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            // Navigate back to MemoMate screen after adding note
                            navController.navigate("memoMate_screen") {
                                popUpTo("memoMate_screen") { inclusive = true } // Clear previous screen
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill in both title and content", Toast.LENGTH_SHORT).show()
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

        Text(text = "Title", style = TextStyle(fontSize = 16.sp))
        TextField(
            value = noteTitle,
            onValueChange = { noteTitle = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        Text(text = "Content", style = TextStyle(fontSize = 16.sp))
        TextField(
            value = noteContent,
            onValueChange = { noteContent = it },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFEEEEEE),
                unfocusedContainerColor = Color(0xFFEEEEEE)
            )
        )
    }
}

@Composable
fun NoteCard(navController: NavHostController, note: MemoMate) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                Log.d("NoteCard", "Navigating to note detail with title: ${note.title}") // Debug log
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

