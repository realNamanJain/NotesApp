package com.example.firebasefirestore.Database

data class Student(
    val name: String = "",
    val email: String = ""
)

data class FirestoreDocument<T>(
    val id: String = "",
    val data: T? = null
)
