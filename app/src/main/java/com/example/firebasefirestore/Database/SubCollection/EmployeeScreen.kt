package com.example.firebasefirestore.Database.SubCollection
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

// State classes to hold fetched data
data class EmployeeData(
    val workHistory: List<WorkHistory> = emptyList(),
    val performanceReviews: List<PerformanceReviews> = emptyList(),
    val trainingSessions: List<TrainingSessions> = emptyList()
)

@Composable
fun EmployeeScreen(firestore: FirebaseFirestore, documentId: String) {
    var employeeData by remember { mutableStateOf(EmployeeData()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch employee data
    LaunchedEffect(Unit) {
        isLoading = true
        fetchEmployeeData(firestore, documentId) { data ->
            employeeData = data
            isLoading = false
        }
    }

    // Show loading or data
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        DisplayEmployeeData(employeeData, documentId)
    }
}

// Composable function to display the employee data
@Composable
fun DisplayEmployeeData(employeeData: EmployeeData, documentId: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Here are the details for $documentId:",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Text(text = "Work History", style = MaterialTheme.typography.bodyLarge,
                fontSize = 12.sp, fontWeight = FontWeight.Bold)
        employeeData.workHistory.forEach { history ->
            Text(text = "${history.jobTitle} at ${history.department} from ${history.startDate} to ${history.endDate}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Performance Reviews", style = MaterialTheme.typography.bodyMedium,fontSize = 12.sp, fontWeight = FontWeight.Bold)
        employeeData.performanceReviews.forEach { review ->
            Text(text = "Date: ${review.reviewDate}, Rating: ${review.rating}, Comments: ${review.comments}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Training Sessions", style = MaterialTheme.typography.bodyMedium,fontSize = 12.sp, fontWeight = FontWeight.Bold)
        employeeData.trainingSessions.forEach { session ->        
            Text(text = "Course: ${session.courseName}, Completed on: ${session.completionDate}, Certification: ${session.certification}")
        }
    }
}
