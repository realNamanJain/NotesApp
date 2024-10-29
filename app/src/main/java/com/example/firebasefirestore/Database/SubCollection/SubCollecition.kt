package com.example.firebasefirestore.Database.SubCollection

import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot

// Data Classes
data class WorkHistory(
    val jobTitle: String = "",
    val department: String = "",
    val startDate: String = "",
    val endDate: String = ""
)

data class PerformanceReviews(
    val reviewDate: String = "",
    val rating: Int = 0,
    val comments: String = ""
)

data class TrainingSessions(
    val courseName: String = "",
    val completionDate: String = "",
    val certification: String = ""
)

data class Conclusion(
    val workHistory: WorkHistory = WorkHistory(),
    val performanceReviews: PerformanceReviews = PerformanceReviews(),
    val trainingSessions: TrainingSessions = TrainingSessions()
)


@Composable
fun AddSubCollection(firestore: FirebaseFirestore) {
    // Create employee data
    val conclusion1 = Conclusion(
        workHistory = WorkHistory("Software Engineer", "IT", "2022-01-01", "2023-12-31"),
        performanceReviews = PerformanceReviews("2023-06-15", 4, "Great performance!"),
        trainingSessions = TrainingSessions("Android Development", "2023-07-10", "Certified Android Developer")
    )

    val conclusion2 = Conclusion(
        workHistory = WorkHistory("Software Engineer", "IT", "2021-01-01", "2022-12-31"),
        performanceReviews = PerformanceReviews("2022-06-15", 4, "Great performance!"),
        trainingSessions = TrainingSessions("Web Development", "2022-07-10", "Certified Web Developer")
    )

    // Save data for both employees
    saveEmployeeData(firestore, "Employee_1", conclusion1)
    saveEmployeeData(firestore, "Employee_2", conclusion2)
}

// Reusable function to save employee data with sub collections
fun saveEmployeeData(firestore: FirebaseFirestore, documentId: String, conclusion: Conclusion) {
    val employeeRef = firestore.collection("Employees").document(documentId)

    // Save each sub collection in FireStore
    saveSubCollection(employeeRef, "WorkHistory", conclusion.workHistory)
    saveSubCollection(employeeRef, "PerformanceReviews", conclusion.performanceReviews)
    saveSubCollection(employeeRef, "TrainingSessions", conclusion.trainingSessions)
}

// Generic function to save any sub collection
fun saveSubCollection(employeeRef: DocumentReference, subCollectionName: String, data: Any) {
    employeeRef.collection(subCollectionName)
        .add(data)
        .addOnSuccessListener {
            Log.d("Firestore", "$subCollectionName added successfully for ${employeeRef.id}")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error adding $subCollectionName for ${employeeRef.id}", e)
        }
}
// Function to fetch employee data
fun fetchEmployeeData(firestore: FirebaseFirestore, documentId: String, onComplete: (EmployeeData) -> Unit) {
    val employeeRef = firestore.collection("Employees").document(documentId)

    val workHistoryList = mutableListOf<WorkHistory>()
    val performanceReviewsList = mutableListOf<PerformanceReviews>()
    val trainingSessionsList = mutableListOf<TrainingSessions>()

    // Fetch Work History
    employeeRef.collection("WorkHistory")
        .get()
        .addOnSuccessListener { workHistorySnapshot ->
            for (doc in workHistorySnapshot.documents) {
                val workHistory = doc.toObject(WorkHistory::class.java)
                if (workHistory != null) {
                    workHistoryList.add(workHistory)
                }
            }
            // Fetch Performance Reviews
            employeeRef.collection("PerformanceReviews")
                .get()
                .addOnSuccessListener { reviewsSnapshot ->
                    for (doc in reviewsSnapshot.documents) {
                        val performanceReview = doc.toObject(PerformanceReviews::class.java)
                        if (performanceReview != null) {
                            performanceReviewsList.add(performanceReview)
                        }
                    }
                    // Fetch Training Sessions
                    employeeRef.collection("TrainingSessions")
                        .get()
                        .addOnSuccessListener { trainingSnapshot ->
                            for (doc in trainingSnapshot.documents) {
                                val trainingSession = doc.toObject(TrainingSessions::class.java)
                                if (trainingSession != null) {
                                    trainingSessionsList.add(trainingSession)
                                }
                            }
                            // Return the aggregated data
                            onComplete(EmployeeData(workHistoryList, performanceReviewsList, trainingSessionsList))
                        }
                }
        }
}