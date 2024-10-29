//package com.example.firebasefirestore.Database.MVVM
//
//import android.util.Log
//import android.widget.Toast
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController // Updated import
//import com.example.firebasefirestore.Database.MVVM.*
//import com.google.firebase.firestore.FirebaseFirestore
//
//@Composable
//fun MemoMateScreen(viewModel: NotesViewModel, repository: AuthRepository, navController: NavHostController) {
//    val context = LocalContext.current
//    val name = repository.getCurrentUserName()
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top
//    ) {
//        Text(text = "MemoMate", style = TextStyle(fontSize = 24.sp), modifier = Modifier.padding(top = 16.dp))
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Button(onClick = { navController.navigate("memo_UI") }, modifier = Modifier.fillMaxWidth()) {
//            Text(text = "+ Add your memo")
//        }
//
//        name?.let {
//            LaunchedEffect(Unit) { viewModel.fetchAllNotes(context, it) }
//            UserNotesScreen(navController = navController, firestore = FirebaseFirestore.getInstance(), userId = it) // Pass firestore and userId
//        }
//    }
//}
//
//// UserNotesScreen.kt
//@Composable
//fun UserNotesScreen(navController: NavHostController, firestore: FirebaseFirestore, userId: String) {
//    val context = LocalContext.current
//    var notes by rememberSaveable { mutableStateOf<List<Notes>>(emptyList()) }
//
//    LaunchedEffect(userId) {
//        fetchAllNotes(context, firestore, userId) { fetchedNotes ->
//            notes = fetchedNotes
//        }
//    }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        LazyColumn {
//            items(notes) { note ->
//                NoteCard(navController = navController, note = note)
//            }
//        }
//    }
//}
//
//@Composable
//fun MemoDetailScreen(navController: NavHostController, firestore: FirebaseFirestore, title: String) {
//    val context = LocalContext.current
//    val name = getCurrentUserName()
//
//    var note by rememberSaveable { mutableStateOf<Notes?>(null) }
//    var documentId by rememberSaveable { mutableStateOf<String?>(null) }
//    var isLoading by rememberSaveable { mutableStateOf(true) }
//
//    LaunchedEffect(title, name) {
//        if (name != null) {
//            fetchNotesWithTitle(context, name, firestore, title) { fetchedNote, id ->
//                note = fetchedNote
//                documentId = id
//                isLoading = false
//            }
//        }
//    }
//
//    if (isLoading) {
//        Text(text = "Loading note...", style = TextStyle(fontSize = 16.sp))
//        return
//    }
//
//    note?.let { currentNote ->
//        var noteTitle by rememberSaveable { mutableStateOf(currentNote.title) }
//        var noteContent by rememberSaveable { mutableStateOf(currentNote.content) }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = "Note Details",
//                    style = TextStyle(fontSize = 24.sp),
//                    modifier = Modifier.align(Alignment.CenterVertically)
//                )
//
//                IconButton(
//                    onClick = {
//                        val noteToSave = Notes(title = noteTitle, content = noteContent)
//                        if (name != null) {
//                            if (documentId != null) {
//                                Log.d("MemoDetailScreen", "Document ID: $documentId")
//                                firestore.collection("Note").document(name)
//                                    .collection("UserNotes").document(documentId!!)
//                                    .set(noteToSave)
//                                    .addOnSuccessListener {
//                                        Toast.makeText(context, "Note updated successfully", Toast.LENGTH_SHORT).show()
//                                        navController.popBackStack()
//                                    }
//                                    .addOnFailureListener { e ->
//                                        Toast.makeText(context, "Error updating note", Toast.LENGTH_SHORT).show()
//                                        Log.e("Firestore", "Error updating note", e)
//                                    }
//                            } else {
//                                addNote(context, firestore, name, noteTitle, noteContent) { success ->
//                                    if (success) {
//                                        Toast.makeText(context, "Note added successfully", Toast.LENGTH_SHORT).show()
//                                    } else {
//                                        Toast.makeText(context, "Error adding note", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            }
//                        }
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Check,
//                        contentDescription = "Save Note",
//                        tint = Color.Black
//                    )
//                }
//            }
//
//            Text(text = "Title", style = TextStyle(fontSize = 16.sp))
//            TextField(
//                value = noteTitle,
//                onValueChange = { noteTitle = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                singleLine = true,
//                textStyle = TextStyle(fontSize = 16.sp)
//            )
//
//            Text(text = "Content", style = TextStyle(fontSize = 16.sp))
//            TextField(
//                value = noteContent,
//                onValueChange = { noteContent = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight(0.7f),
//                textStyle = TextStyle(fontSize = 16.sp),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = Color(0xFFEEEEEE),
//                    unfocusedContainerColor = Color(0xFFEEEEEE)
//                )
//            )
//        }
//    } ?: run {
//        Text(text = "Note not found", style = TextStyle(fontSize = 16.sp))
//    }
//}
//
//@Composable
//fun NoteCard(navController: NavHostController, note: Notes) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//            .clickable {
//                navController.navigate("memo_detailScreen/${note.title}")
//            },
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Text(
//            text = note.title,
//            modifier = Modifier.padding(16.dp),
//            style = MaterialTheme.typography.titleMedium
//        )
//    }
//}
