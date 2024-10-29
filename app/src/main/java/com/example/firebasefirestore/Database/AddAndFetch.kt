package com.example.firebasefirestore.Database

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun AddAndFetch(firestore: FirebaseFirestore){
    val context = LocalContext.current
    val academic = hashMapOf<String, Any>(
        "Subjects" to "Maths",
        "Marks" to 95,
        "Grade" to "A"
    )
    addData(firestore,"academic",academic,context)
    val sports = hashMapOf<String, Any>(
        "Subjects" to "Cricket",
        "Marks" to 95,
        "Grade" to "A"
    )
    addData(firestore,"sports",sports,context)
    val attendance = hashMapOf<String, Any>(
        "totalAttendance" to 180,
        "totalAbsentDays" to 95
    )
    addData(firestore,"Attendance",attendance,context)
}
@Composable
fun ShowAllDataScreen(firestore: FirebaseFirestore) {
    var academicData by remember { mutableStateOf<List<Pair<String, Map<String, Any>>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch academic data
    LaunchedEffect(Unit) {
        fetchData(firestore) { result ->
            academicData = result
            isLoading = false
        }
    }

    // UI to display the data
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(academicData) { (documentId, data) ->  // Destructure the Pair
                    Column {
                        Text("Document ID: $documentId")
                        // Loop through the document's fields dynamically
                        data.forEach { (key, value) ->
                            Text("$key: $value")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}


fun addData(firestore: FirebaseFirestore,document: String, students: HashMap<String, Any>, context: Context){
    firestore.collection("students").document(document).set(students).addOnSuccessListener {
        Toast.makeText(context, "User added successfully with ID: $document", Toast.LENGTH_SHORT).show()
        android.util.Log.d("Firestore", "User added successfully with ID: $document")
    }.addOnFailureListener {
        Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show()
        android.util.Log.e("FireStore", "Error in adding user: ", it)
    }
}

fun fetchData(firestore: FirebaseFirestore, callback: (List<Pair<String, Map<String, Any>>>) -> Unit) {
    firestore.collection("Students").document("Naman Jain").collection("academic")
        .get()
        .addOnSuccessListener { result ->
            val dataList = mutableListOf<Pair<String, Map<String, Any>>>()
            for (document in result) {
                // Add a Pair of document ID and its data to the list
                dataList.add(Pair(document.id, document.data))
            }
            callback(dataList)  // Pass the list to the callback
        }
        .addOnFailureListener { exception ->
            android.util.Log.e("Firestore", "Error getting documents: ", exception)
            callback(emptyList())  // Return an empty list on failure
        }
}