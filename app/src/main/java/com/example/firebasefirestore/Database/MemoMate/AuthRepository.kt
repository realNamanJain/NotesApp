package com.example.firebasefirestore.Database.MemoMate

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

// Repository class for handling authentication tasks
class AuthRepository(private val auth: FirebaseAuth) {

    // Function to sign up a user with email and password
    suspend fun signUpWithEmailPassword(email: String, password: String): Result<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await() // Attempt to create user in Firebase
            Result.success(true) // Return success if sign-up is successful
        } catch (e: Exception) {
            Result.failure(e) // Return failure if there's an exception
        }
    }

    // Function to log in a user with email and password
    suspend fun loginWithEmailPassword(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await() // Attempt to log in user
            Result.success(true) // Return success if login is successful
        } catch (e: Exception) {
            Result.failure(e) // Return failure if there's an exception
        }
    }

    // Function to send a password reset email
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await() // Send password reset email
            Result.success(Unit) // Return success if email is sent
        } catch (e: Exception) {
            Result.failure(e) // Return failure if there's an exception
        }
    }

    // Function to get the current user's name (derived from their email before the '@' symbol)
    fun getCurrentUserName(): String? {
        val user = auth.currentUser // Get the currently logged-in user
        return user?.email?.let { email ->
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                email.substringBefore("@") // Extract and return the part before '@' if it's a valid email
            } else null
        }
    }

    // Function to log out the current user
    fun signOut() {
        auth.signOut() // Signs out the current user
    }
}
