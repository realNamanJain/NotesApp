package com.example.firebasefirestore.Database

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.firebasefirestore.TAG
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun practice(firestore: FirebaseFirestore) {
    val context = LocalContext.current
    val academicData = hashMapOf<String, Any>(
        "Subjects" to "Maths",
        "Marks" to 95,
        "Grade" to "A"
    )
    val sportsData = hashMapOf<String, Any>(
        "Subjects" to "Cricket",
        "Marks" to 95,
        "Grade" to "A"
    )
    val attendanceData = hashMapOf<String, Any>(
        "totalAttendance" to 180,
        "totalAbsentDays" to 95
    )
    addData("academic",firestore,academicData){id ->
        if(id!=null){
            Toast.makeText(context, "User added successfully with ID: $id", Toast.LENGTH_SHORT).show()
        }else{
            Log.d(TAG, "Failed to add user")
            Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show()
        }
    }

    addData("sports",firestore,sportsData){sid ->
        if(sid!=null){
            Log.d(TAG, "User added successfully with ID: $sid")
            Toast.makeText(context, "User added successfully with ID: $sid", Toast.LENGTH_SHORT).show()
        }else{
            Log.d(TAG, "Failed to add user")
            Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show()
        }
    }
    addData("Attendance",firestore,attendanceData){aid ->
        if(aid!=null){
            Log.d(TAG, "User added successfully with ID: $aid")
            Toast.makeText(context, "User added successfully with ID: $aid", Toast.LENGTH_SHORT).show()
        }else{
            Log.d(TAG, "Failed to add user")
            Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show()
        }
    }

}

fun addData(document: String,firestore: FirebaseFirestore, students: HashMap<String, Any>, callback: (String?) -> Unit){
    val documentName=document
    firestore.collection("students").document(documentName).set(students).addOnSuccessListener {
        Log.d(TAG, "User added successfully with ID: $documentName")
        callback(documentName)
    }.addOnFailureListener {
        Log.e(TAG, "Error in adding user: ", it)
        callback(null)
    }

    //set(students) in this students is the collection name and document(documentId) is the document name
}

fun addStudentsDataWithBatch(firestore: FirebaseFirestore, studentId: String){
    val batch = firestore.batch()
    val academicRef = firestore.collection("Students").document(studentId).collection("academic")
        .document()
    val academicData= hashMapOf<String, Any>(
        "Subjects" to "Maths",
        "Marks" to 5,
        "Grade" to "F"
    )
    batch.set(academicRef,academicData)

    val sportsRef = firestore.collection("Students").document(studentId).collection("sports")
        .document()
    val sportsData= hashMapOf<String, Any>(
        "Subjects" to "Cricket",
        "Marks" to 65,
        "Grade" to "B"
    )
    batch.set(sportsRef,sportsData)

    val attendanceRef = firestore.collection("Students").document(studentId).collection("Attendance")
        .document()
    val attendanceData= hashMapOf<String, Any>(
        "totalAttendance" to 180,
        "totalAbsentDays" to 125
    )
    batch.set(attendanceRef,attendanceData)

    batch.commit().addOnSuccessListener {
        Log.d(TAG, "All data added successfully in batch!")
    }.addOnFailureListener { e ->
        Log.e(TAG, "Error adding data in batch", e)
    }
}

@Composable
fun ShowDataScreen(firestore: FirebaseFirestore) {
    var students by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Call the Firestore function to get data
    LaunchedEffect(Unit) {
        getData(firestore) { result ->
            students = result
            isLoading = false
        }
    }

    // UI to display the data
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            students.forEach { student ->
                Text("Name: ${student["name"]}")
                Text("Roll No: ${student["roll no"]}")
                Text("Branch: ${student["branch"]}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


fun getData(firestore: FirebaseFirestore, callback: (List<Map<String, Any>>) -> Unit){
    firestore.collection("Students")
        .get().addOnSuccessListener { result ->
            Log.w("firestore", "Documents retrieve successfully")
            val studentList = mutableListOf<Map<String, Any>>()
            for(document in result){
                studentList.add(document.data)
            }
            callback(studentList)
        }
        .addOnFailureListener { exception ->
            Log.w("Firestore", "Error getting documents: ", exception)
            callback(emptyList())
        }
}

@Preview(showBackground = true)
@Composable
fun ShowDataScreenPreview() {
    // Pass a dummy Firestore instance for the preview
    val dummyFirestore = FirebaseFirestore.getInstance()

    // Calling the actual ShowDataScreen with the dummy Firestore instance
    ShowDataScreen(firestore = dummyFirestore)
}