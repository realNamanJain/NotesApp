//package com.example.firebasefirestore.Database.NotesApp
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
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
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.google.firebase.firestore.FirebaseFirestore
//
////this function shows the note screen to the new note
//@Composable
//fun NoteUI(firestore: FirebaseFirestore) {
//    val context = LocalContext.current
//    var noteTitle by rememberSaveable { mutableStateOf("") }
//    var noteContent by rememberSaveable { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = "Note Details",
//                style = TextStyle(fontSize = 24.sp),
//                modifier = Modifier.align(Alignment.CenterVertically)
//            )
//
//            IconButton(
//                onClick = {
//                    // This will trigger the function to save the note (non-composable function)
////                    addNote(context, firestore, noteTitle, noteContent) { success ->
////                        if (success) {
////                             // Trigger the side-effect in Compose
////                            Toast.makeText(context, "Note added successfully", Toast.LENGTH_SHORT).show()
////                        } else {
////                            Toast.makeText(context, "Error in adding note", Toast.LENGTH_SHORT).show()
////                        }
////                    }
//                }
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Check,
//                    contentDescription = "Save Note",
//                    tint = Color.Black
//                )
//            }
//        }
//
//        // Title TextField
//        Text(text = "Title", style = TextStyle(fontSize = 16.sp))
//        TextField(
//            value = noteTitle,
//            onValueChange = { noteTitle = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            singleLine = true,
//            textStyle = TextStyle(fontSize = 16.sp)
//        )
//
//        // Content TextField
//        Text(text = "Content", style = TextStyle(fontSize = 16.sp))
//        TextField(
//            value = noteContent,
//            onValueChange = { noteContent = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(0.7f),
//            textStyle = TextStyle(fontSize = 16.sp),
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = Color(0xFFEEEEEE),
//                unfocusedContainerColor = Color(0xFFEEEEEE)
//            )
//        )
//    }
//}
//
////this function shows the note screen to the existing note
//@Composable
//fun NoteDetailScreen(firestore: FirebaseFirestore, title: String) {
//    val context = LocalContext.current
//    var note by rememberSaveable { mutableStateOf<Notes?>(null) }
//
//    // Fetch the note based on title when the Composable is first composed
//    fetchNotesWithTitle(context, firestore, title) { fetchedNote ->
//        note = fetchedNote
//    }
//
//    // Display the note details if the note has been fetched
//    note?.let { currentNote ->
//        var noteTitle by rememberSaveable { mutableStateOf(currentNote.title) }
//        var noteContent by rememberSaveable { mutableStateOf(currentNote.content) }
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
//                        // Save note functionality
////                        addNote(context, firestore, noteTitle, noteContent) { success ->
////                            if (success) {
////                                Toast.makeText(context, "Note added successfully", Toast.LENGTH_SHORT).show()
////                            } else {
////                                Toast.makeText(context, "Error in adding note", Toast.LENGTH_SHORT).show()
////                            }
////                        }
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
//            // Title TextField
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
//            // Content TextField
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
//        // Show loading or error state if note is not fetched yet
//        Text(text = "Loading note...", style = TextStyle(fontSize = 16.sp))
//    }
//}
//
////this function shows the all notes to the screen
//@Composable
//fun NotesScreen(navController: NavController, firestore: FirebaseFirestore) {
//    val context = LocalContext.current
//    var notesList by remember { mutableStateOf<List<Notes>>(emptyList()) }
//
//    // Fetch all notes on launch
//    LaunchedEffect(Unit) {
////        fetchAllNotes(context, firestore) { notes ->
////            notesList = notes
////        }
//    }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        if (notesList.isEmpty()) {
//            Text(text = "No notes available", modifier = Modifier.padding(16.dp))
//        } else {
//            notesList.forEach { note ->
//                NoteCard(
//                    navController = navController,
//                    note = note
//                )
//            }
//        }
//    }
//}
//
//
////this function is making an ui for every note
//@Composable
//fun NoteCard(
//    navController: NavController,
//    note: Notes,
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//            .clickable {
//                // Trigger navigation with the note title
//                navController.navigate("note_detail_screen/${note.title}")
//            },
//        shape = RoundedCornerShape(8.dp),
//        border = BorderStroke(1.dp, Color.LightGray),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            horizontalAlignment = Alignment.Start
//        ) {
//            Text(
//                text = note.title,
//                style = TextStyle(fontSize = 20.sp, color = Color.Black)
//            )
//        }
//    }
//}
//
//
//@Composable
//@Preview(showBackground = true, showSystemUi = true)
//fun Preview(){
////    NoteUI()
//
//}
