package com.example.firebasefirestore

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firebasefirestore.Database.MemoMate.AuthRepository
import com.example.firebasefirestore.Database.MemoMate.CardItem
import com.example.firebasefirestore.Database.MemoMate.MemoMateRepository
import com.example.firebasefirestore.Database.MemoMate.MemoMateViewModel
import com.example.firebasefirestore.Database.MemoMate.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Constants for logging
const val TAG = "FirestoreExample"

class MainActivity : ComponentActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase FireStore and Auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        setContent {
//            FirestoreApp(firestore)
//            practice(firestore)
//            val name = "Golu singh"
//            addStudentsDataWithBatch(firestore,name)
//            ShowDataScreen(firestore = firestore)
//            DynamicWalletCard()
//            AddAndFetch(firestore = firestore)
//            ShowAllDataScreen(firestore = firestore)
//            AddSubCollection(firestore = firestore)
//            EmployeeScreen(firestore = firestore, documentId = "Employee_1")
//            addNotes(firestore)
//            DisplayNotes(firestore = firestore)
//            NoteUI(firestore = firestore)
//            MainNavHost(firestore)
//            AuthScreen(firestore)
//            MemoUI(firestore = firestore)
//            UserNotesScreen(firestore, "naman")
            Navigation(auth, firestore)
//            CardItem("First Note","he title font size is set to 20.sp, and the content font size is set to 16.sp. This helps differentiate the title from the content while making sure both fit well within the card.")

        }
    }
}

@Composable
fun FirestoreApp(firestore: FirebaseFirestore) {
    // States for user input
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) } // State to hold user list
    val context = LocalContext.current

    // Snackbar state for Material 3
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Loading state
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) } // Display SnackbarHost here
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("FireStore Example", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                isError = name.isBlank(), // Show error if name is empty
                modifier = Modifier.fillMaxWidth()
            )
            if (name.isBlank()) {
                Text("Name cannot be empty", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Age Input (number only)
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = age.toIntOrNull() == null, // Show error if age is not a number
                modifier = Modifier.fillMaxWidth()
            )
            if (age.toIntOrNull() == null) {
                Text("Please enter a valid age", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                isError = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(), // Email validation
                modifier = Modifier.fillMaxWidth()
            )
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Text("Please enter a valid email", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add user button with loading indicator
            Button(onClick = {
                val ageInt = age.toIntOrNull()
                val isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

                if (name.isNotBlank() && ageInt != null && isValidEmail) {
                    coroutineScope.launch {
                        isLoading = true // Show loading indicator
                        val user = hashMapOf<String, Any>(
                            "name" to name,
                            "age" to ageInt,
                            "email" to email
                        )
                        addUser(firestore, user) { id ->
                            isLoading = false // Hide loading indicator
                            if (id != null) {
                                userId = id
                                snackbarMessage="User added successfully!"
                            } else {
                                snackbarMessage = "Failed to add user."
                            }
                        }
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Please enter valid inputs!")
                    }
                }
            }) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp)) // Small loading indicator
                } else {
                    Text("Add User")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to get users from FireStore
            Button(onClick = {
                coroutineScope.launch { // Launch coroutine to get users
                    getUsers(firestore,context) { fetchedUsers ->
                        users = fetchedUsers // Update users state
                    }
                }
            }) {
                Text("Get Users")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (userId.isNotEmpty()) {
                // Button to delete a user by ID
                Button(onClick = {
                    coroutineScope.launch {
                        isLoading = true // Show loading indicator
                        deleteUser(firestore, userId)
                        isLoading = false // Hide loading indicator
                        snackbarHostState.showSnackbar("User deleted")
                        userId = ""  // Reset the user ID
                    }
                }) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Delete User")
                    }
                }
            }

            // Display fetched users
            Spacer(modifier = Modifier.height(16.dp))
            Text("Fetched Users:", style = MaterialTheme.typography.bodyMedium)
            users.forEach { user ->
                Text("User ID: ${user.id}, Properties: ${user.properties}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

data class User(
    val id: String,
    val properties: Map<String, Any?> // Dynamic properties
)

suspend fun addUser(firestore: FirebaseFirestore, user: HashMap<String, Any>, callback: (String?) -> Unit) {

//    try {
//        val documentReference = firestore.collection("users").add(user).await()
//        Log.d(TAG, "User added with ID: ${documentReference.id}")
//        callback(documentReference.id)
//    } catch (e: Exception) {
//        Log.e(TAG, "Error adding user: ", e)
//        callback(null)
//    }
    try {
        val documentId = "customUserId" // Specify a custom document ID
        firestore.collection("users").document(documentId).set(user).await()
        Log.d(TAG, "User added with custom ID: $documentId")
        callback(documentId)
    } catch (e: Exception) {
        Log.e(TAG, "Error adding user: ", e)
        callback(null)
    }
}

suspend fun getUsers(firestore: FirebaseFirestore, context: Context, callback: (List<User>) -> Unit) {
    try {
        val result = firestore.collection("users").get().await() // Use await for suspend function
        val fetchedUsers = result.documents.map { document ->
            // Create a map for dynamic fields
            val properties = mutableMapOf<String, Any?>()
            for (field in document.data?.keys ?: emptySet()) {
                properties[field] = document.get(field)
            }
            User(
                id = document.id,
                properties = properties
            )
        }
        callback(fetchedUsers) // Return the list of fetched users
    } catch (e: Exception) {
        Log.e(TAG, "Error getting users: ", e)
        Toast.makeText(context, "Failed to load users", Toast.LENGTH_SHORT).show()
    }
}

suspend fun deleteUser(firestore: FirebaseFirestore, userId: String) {
    try {
        firestore.collection("users").document(userId).delete().await()
        Log.d(TAG, "User deleted")
    } catch (e: Exception) {
        Log.w(TAG, "Error deleting user", e)
    }
}

@Preview(showBackground = true)
@Composable
fun FirestoreAppPreview() {
    // Dummy Firestore instance for preview purposes
    FirestoreApp(firestore = FirebaseFirestore.getInstance())
}
