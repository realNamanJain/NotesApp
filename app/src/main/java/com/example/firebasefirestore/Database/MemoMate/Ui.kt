package com.example.firebasefirestore.Database.MemoMate

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun StartingScreen(authViewModel: AuthViewModel, viewModel: MemoMateViewModel, repository: AuthRepository, navController: NavHostController) {
    // Get the current user's name from the repository
    val name = repository.getCurrentUserName()

    // Observe the notes list from the ViewModel
    val notes by viewModel.notesList.observeAsState(emptyList())

    // Fetch all notes when the screen is first composed
    name?.let {
        LaunchedEffect(it) {
            viewModel.fetchAllNotes(it) // Start fetching notes for the user
        }
    }

    // Box to hold the main UI components
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF7874A)) // Set background color
    ) {
        // Column for arranging elements vertically
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar component for navigation and app title
            TopBar(navController, authViewModel)
            Spacer(modifier = Modifier.height(16.dp)) // Spacer for vertical spacing

            // Notes Grid for displaying notes in a grid format
            NotesGrid(notes = notes.map { it.title to it.content }, navController = navController) // Convert notes to pairs of title and content
        }

        // Floating Action Button to add a new note
        FloatingActionButton(
            onClick = { navController.navigate("memo_UI") }, // Navigate to note writing screen
            containerColor = Color(0xFFF15A09), // Set FAB color
            modifier = Modifier.size(93.dp, 86.dp) // Set FAB size
                .align(Alignment.BottomEnd) // Align to the bottom end
                .padding(16.dp) // Add padding from the edges
        ) {
            // Icon for the FAB
            Icon(
                imageVector = Icons.Default.Add, // Use your add icon resource
                contentDescription = "Add", // Content description for accessibility
                tint = Color.White, // Set icon color to white
                modifier = Modifier.size(60.dp) // Set icon size
            )
        }
    }
}

@Composable
fun TopBar(navController: NavHostController, viewModel: AuthViewModel) {
    // Row for placing elements horizontally at the top of the screen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(71.dp)
            .background(Color(0xFFF7874A)), // Background color for the top bar
        verticalAlignment = Alignment.CenterVertically // Center align items vertically
    ) {
        // Menu Icon for navigation to account screen
        IconButton(
            onClick = { navController.navigate("accountScreen") }, // Navigate to account screen
            modifier = Modifier.size(60.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle, // Menu icon
                contentDescription = "Menu", // Content description for accessibility
                modifier = Modifier.size(40.dp) // Set icon size
            )
        }

        // App Title with weight to fill the remaining space
        Text(
            text = "Memo Mate", // App title text
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontSize = 28.sp, // Font size for title
                color = Color.Black // Text color
            ),
            modifier = Modifier
                .weight(1f) // Give it a weight to occupy the remaining space
                .fillMaxWidth(), // Allow text to fill the width
            textAlign = TextAlign.Center // Center text
        )

        // Logout Icon for signing out
        IconButton(
            onClick = {
                viewModel.signOut() // Trigger sign out
                navController.navigate("authScreen") // Navigate to login screen
            },
            modifier = Modifier.size(60.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp, // Logout icon
                contentDescription = "Logout", // Content description for accessibility
                modifier = Modifier.size(40.dp) // Set icon size
            )
        }
    }
}

@Composable
fun NotesGrid(notes: List<Pair<String, String>>, navController: NavHostController) {
    // LazyVerticalGrid for displaying notes in a grid layout
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Set 2 columns for the grid
        modifier = Modifier.fillMaxSize(), // Fill maximum available space
        contentPadding = PaddingValues(16.dp), // Padding around the grid
        verticalArrangement = Arrangement.spacedBy(16.dp), // Space between rows
        horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between columns
    ) {
        // Iterate through notes and create a CardItem for each note
        items(notes.size) { index ->
            val (title, content) = notes[index] // Destructure title and content
            // Adding padding around each CardItem
            CardItem(
                title = title, // Pass title to CardItem
                content = content, // Pass content to CardItem
                navController = navController // Pass navController for navigation
            )
        }
    }
}

@Composable
fun CardItem(title: String, content: String, navController: NavHostController) {
    // Card for displaying individual note
    Card(
        modifier = Modifier
            .width(181.dp) // Width of the card
            .height(145.dp) // Height of the card
            .clickable { navController.navigate("memo_detailScreen/$title") }, // Navigate to detail screen on click
        shape = RoundedCornerShape(20.dp), // Rounded corners for the card
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Card elevation for shadow effect
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBDBC0)) // Set background color here
        ) {
            // Column for arranging title and content vertically
            Column(
                modifier = Modifier.padding(16.dp), // Padding inside the card
                verticalArrangement = Arrangement.SpaceBetween // Space items evenly
            ) {
                // Title Text
                Text(
                    text = title, // Title text
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 24.sp, // Font size for title
                        color = Color.Black, // Text color
                        fontWeight = FontWeight.Bold // Bold font for title
                    ),
                    maxLines = 1, // Show only 1 line for the title
                    overflow = TextOverflow.Ellipsis // Show ellipsis if text is too long
                )
                Spacer(modifier = Modifier.height(6.dp)) // Spacer for vertical spacing
                // Content Text
                Text(
                    text = content, // Content text
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp, // Font size for content
                        color = Color.Black // Text color
                    ),
                    maxLines = 3, // Show up to 3 lines of content
                    overflow = TextOverflow.Ellipsis // Show ellipsis if text is too long
                )
            }
        }
    }
}

@Composable
fun AccountScreen(repository: AuthRepository, navController: NavHostController) {
    // Get the current user's name from the repository
    val name = repository.getCurrentUserName()
    // Box for the main content of the AccountScreen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7874A)) // Background color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(36.dp), // Padding at the top
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Align items to the top
        ) {
            // Icon Button at the top left to navigate back
            IconButton(
                onClick = {
                    // Navigate back or perform any action here
                    navController.popBackStack() // Example action
                },
                modifier = Modifier.align(Alignment.Start) // Align to the start
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Back icon
                    contentDescription = "Back", // Content description for accessibility
                    tint = Color.Black // Change icon color if needed
                )
            }

            // Frame containing the account image
            Box(
                modifier = Modifier
                    .size(268.dp) // Frame size
                    .clip(RoundedCornerShape(8.dp)) // Rounded corners
                    .background(Color.Transparent) // Ensure it is transparent
            ) {
                // Image of the account
                Image(
                    imageVector = Icons.Default.AccountCircle, // Use account icon as a placeholder
                    contentDescription = null, // No description needed for icons
                    modifier = Modifier
                        .fillMaxSize() // Fill the entire frame
                        .padding(22.dp) // Margin around the image
                        .clip(RoundedCornerShape(8.dp)), // Rounded corners for the image
                    contentScale = ContentScale.Crop // Adjust image scaling
                )
            }

            // Display user's name if available
            if (name != null) {
                Text(
                    text = name, // Display user's name
                    modifier = Modifier
                        .padding(top = 40.dp) // Margin from the top
                        .align(Alignment.CenterHorizontally), // Center align
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 24.sp, // Font size for the name
                        color = Color.Black // Text color
                    )
                )
            }

            // Logout button to sign out
            Button(
                onClick = {
                    repository.signOut() // Trigger sign out
                    navController.navigate("authScreen") // Navigate to auth screen
                },
                modifier = Modifier
                    .padding(top = 40.dp)
                    .align(Alignment.CenterHorizontally) // Center align the button
            ) {
                Text(text = "Logout") // Button text
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoWritingScreen(viewModel: MemoMateViewModel, repository: AuthRepository, navController: NavHostController) {
    val name = repository.getCurrentUserName() // Fetch the current user's name from the repository
    var titleText by remember { mutableStateOf("") } // State for the memo title
    var contentText by remember { mutableStateOf("") } // State for the memo content
    var isMemoSaved by remember { mutableStateOf(false) } // State to indicate if the memo has been saved
    val context = LocalContext.current // Get the current context

    // Main container for the screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7874A)) // Set background color for the screen
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp) // Padding around the column
        ) {
            // Row containing the header text and the check icon for saving the memo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Space out elements in the row
                verticalAlignment = Alignment.Top // Align items to the top
            ) {
                Text(
                    text = "Add your memo here", // Header text
                    color = Color.Black,
                    fontSize = 26.sp,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 40.sp
                )
                Box(
                    modifier = Modifier
                        .size(40.dp) // Size for the check icon box
                ) {
                    // Check icon for saving the memo
                    Image(
                        imageVector = Icons.Default.Check, // Vector asset for the check icon
                        contentDescription = null, // No description for the image
                        modifier = Modifier
                            .size(40.dp, 40.dp) // Size of the icon
                            .padding(top = 10.dp, start = 6.dp) // Padding for positioning
                            .clickable {
                                // Handle memo saving when icon is clicked
                                if (name != null && titleText.isNotEmpty() && contentText.isNotEmpty()) {
                                    isMemoSaved = true // Set save status to true
                                    viewModel.addNote(name, titleText, contentText) { success ->
                                        val message = if (success) {
                                            // Clear input fields if note was added successfully
                                            titleText = ""
                                            contentText = ""
                                            "Note added successfully" // Success message
                                        } else {
                                            "Error in adding note" // Error message
                                        }
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show() // Show toast message
                                        // Navigate back to MemoMate screen after adding note
                                        navController.navigate("memoMate_screen") {
                                            popUpTo("memoMate_screen") { inclusive = true } // Clear previous screen
                                        }
                                    }
                                } else {
                                    // Prompt user to fill in title and content if fields are empty
                                    Toast.makeText(context, "Please fill in both title and content", Toast.LENGTH_SHORT).show()
                                }
                            },
                        contentScale = ContentScale.FillBounds // Scale the icon to fill the bounds
                    )
                }
            }

            // Title section
            Text(
                text = "Title", // Label for the title input
                color = Color.Black,
                fontSize = 38.sp,
                fontFamily = FontFamily.Serif,
                lineHeight = 45.562.sp,
                modifier = Modifier.padding(top = 37.dp) // Padding for the title label
            )
            // Title TextField for user input
            TextField(
                value = titleText,
                onValueChange = { titleText = it }, // Update title state on text change
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(Color(0xFFF7874A).copy(alpha = 0.7f)) // Background color for the TextField
                    .border(1.dp, Color.Black), // Border for the TextField
                placeholder = { Text("Enter title") }, // Placeholder text
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // No indicator when focused
                    unfocusedIndicatorColor = Color.Transparent, // No indicator when unfocused
                    cursorColor = Color.Black, // Cursor color
                    containerColor = Color(0xFFF7874A).copy(alpha = 0.7f) // Background color
                )
            )

            // Content section
            Text(
                text = "Content", // Label for the content input
                color = Color.Black,
                fontSize = 38.sp,
                fontFamily = FontFamily.Serif,
                lineHeight = 45.562.sp,
                modifier = Modifier.padding(top = 28.dp) // Padding for the content label
            )
            // Content TextField for user input
            TextField(
                value = contentText,
                onValueChange = { newText -> // Update content state on text change
                    // Check word count limit (150 words)
                    val wordCount = newText.trim().split("\\s+".toRegex()).size
                    if (wordCount <= 150) { // Limit to 150 words
                        contentText = newText
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(570.dp) // Height of the content TextField
                    .padding(top = 8.dp)
                    .background(Color(0xFFF7874A).copy(alpha = 0.7f)) // Background color
                    .border(1.dp, Color.Black), // Border for the TextField
                placeholder = { Text("Enter content") }, // Placeholder text
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // No indicator when focused
                    unfocusedIndicatorColor = Color.Transparent, // No indicator when unfocused
                    cursorColor = Color.Black, // Cursor color
                    containerColor = Color(0xFFF7874A).copy(alpha = 0.7f) // Background color
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: MemoMateViewModel,
    repository: AuthRepository,
    navController: NavHostController,
    title: String // Title passed to the screen for editing
) {
    val name = repository.getCurrentUserName() // Fetch the current user's name from the repository
    var titleText by remember { mutableStateOf("") } // State for the memo title
    var contentText by remember { mutableStateOf("") } // State for the memo content
    var isMemoSaved by remember { mutableStateOf(false) } // State to indicate if the memo has been saved
    var isLoading by remember { mutableStateOf(true) } // State to indicate loading status
    val context = LocalContext.current // Get the current context

    // Fetch existing note by title when the screen is launched
    LaunchedEffect(title, name) {
        if (name != null) {
            viewModel.fetchNoteWithTitle(name, title) // Fetch the note if title is provided
        }
    }

    // Observe the current note
    val note by viewModel.currentNote.observeAsState()

    // Update the state when the note changes
    LaunchedEffect(note) {
        note?.let {
            titleText = it.title // Set title from fetched note
            contentText = it.content // Set content from fetched note
            isLoading = false // Set loading status to false
        }
    }

    // UI Structure
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7874A)) // Background color for the screen
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp) // Padding around the column
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Space out elements in the row
                verticalAlignment = Alignment.Top // Align items to the top
            ) {
                Text(
                    text = if (note != null) "Edit your memo" else "Add your memo here", // Conditional header text
                    color = Color.Black,
                    fontSize = 26.sp,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 40.sp
                )
                Box(
                    modifier = Modifier.size(40.dp) // Size for the check icon box
                ) {
                    // Check icon for saving the memo
                    Image(
                        imageVector = Icons.Default.Check, // Vector asset for the check icon
                        contentDescription = null, // No description for the image
                        modifier = Modifier
                            .size(40.dp, 40.dp) // Size of the icon
                            .padding(top = 10.dp, start = 6.dp) // Padding for positioning
                            .clickable {
                                val documentId = viewModel.currentNoteId // Get the current document ID

                                // Check if the user name and document ID are available
                                if (name != null && documentId != null) {
                                    viewModel.updateNote(name, documentId, contentText) { success ->
                                        if (success) {
                                            // Show success message and navigate back
                                            Toast.makeText(context, "Note updated successfully", Toast.LENGTH_SHORT).show()
                                            Log.d("MemoMate", "Update successful")
                                            navController.navigate("memoMate_screen") {
                                                popUpTo("memoMate_screen") { inclusive = true } // Clear previous screen
                                            }
                                        } else {
                                            Toast.makeText(context, "Error in updating note", Toast.LENGTH_SHORT).show() // Show error message
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show() // Error fetching data
                                }
                            },
                        contentScale = ContentScale.FillBounds // Scale the icon to fill the bounds
                    )
                }
            }

            // Title section
            Text(
                text = "Title", // Label for the title input
                color = Color.Black,
                fontSize = 38.sp,
                fontFamily = FontFamily.Serif,
                lineHeight = 45.562.sp,
                modifier = Modifier.padding(top = 37.dp) // Padding for the title label
            )
            // Title TextField for user input
            TextField(
                value = titleText,
                onValueChange = { titleText = it }, // Update title state on text change
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(Color(0xFFF7874A).copy(alpha = 0.7f)) // Background color for the TextField
                    .border(1.dp, Color.Black), // Border for the TextField
                placeholder = { Text("Enter title") }, // Placeholder text
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // No indicator when focused
                    unfocusedIndicatorColor = Color.Transparent, // No indicator when unfocused
                    cursorColor = Color.Black, // Cursor color
                    containerColor = Color(0xFFF7874A).copy(alpha = 0.7f) // Background color
                )
            )

            // Content section
            Text(
                text = "Content", // Label for the content input
                color = Color.Black,
                fontSize = 38.sp,
                fontFamily = FontFamily.Serif,
                lineHeight = 45.562.sp,
                modifier = Modifier.padding(top = 28.dp) // Padding for the content label
            )
            // Content TextField for user input
            TextField(
                value = contentText,
                onValueChange = { newText -> // Update content state on text change
                    // Check word count limit (150 words)
                    val wordCount = newText.trim().split("\\s+".toRegex()).size
                    if (wordCount <= 150) { // Limit to 150 words
                        contentText = newText
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(570.dp) // Height of the content TextField
                    .padding(top = 8.dp)
                    .background(Color(0xFFF7874A).copy(alpha = 0.7f)) // Background color
                    .border(1.dp, Color.Black), // Border for the TextField
                placeholder = { Text("Enter content") }, // Placeholder text
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // No indicator when focused
                    unfocusedIndicatorColor = Color.Transparent, // No indicator when unfocused
                    cursorColor = Color.Black, // Cursor color
                    containerColor = Color(0xFFF7874A).copy(alpha = 0.7f) // Background color
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
//    MainScreen()
//    HomeScreen()
//    MemoWritingScreen()
//    val navController = rememberNavController()
//    TopBar(navController)
}
