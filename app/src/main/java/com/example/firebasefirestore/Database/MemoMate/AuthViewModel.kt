package com.example.firebasefirestore.Database.MemoMate

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

// ViewModel for handling user authentication
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    // LiveData to observe authentication state
    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    init {
        // Check if the user is already logged in
        checkAutoLogin()
    }

    // Check for existing user session for auto-login
    private fun checkAutoLogin() {
        val user = FirebaseAuth.getInstance().currentUser
        _authState.value = if (user != null) AuthState.Success else AuthState.Idle
    }

    // Job for handling auto logout timing
    private var autoLogoutJob: Job? = null

    // Sign up a new user with email and password
    fun signUp(email: String, password: String) {
        // Validate email and password
        if (!isEmailValid(email) || password.length < 6) {
            _authState.value = AuthState.Error("Invalid email or password.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Attempt to sign up and update the auth state based on the result
            val result = authRepository.signUpWithEmailPassword(email, password)
            _authState.value = if (result.isSuccess) AuthState.Success else AuthState.Error(result.exceptionOrNull()?.message ?: "Sign-up failed")
        }
    }

    // Log in an existing user with email and password
    fun login(email: String, password: String) {
        // Validate email and password
        if (!isEmailValid(email) || password.length < 6) {
            _authState.value = AuthState.Error("Invalid email or password.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Attempt to log in and manage auto logout timer if successful
            val result = authRepository.loginWithEmailPassword(email, password)
            _authState.value = if (result.isSuccess) {
                startAutoLogoutTimer()
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    // Start a timer for automatic logout after a specified duration
    private fun startAutoLogoutTimer() {
        autoLogoutJob?.cancel() // Cancel any existing timer
        autoLogoutJob = viewModelScope.launch {
            delay(AUTO_LOGOUT_DELAY) // Wait for the specified duration
            signOut() // Automatically log out the user
            _authState.value = AuthState.AutoLoggedOut // Update state to indicate auto-logout
        }
    }

    // Reset the user's password
    fun resetPassword(email: String) {
        // Validate email format
        if (!isEmailValid(email)) {
            _authState.value = AuthState.Error("Please enter a valid email.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Attempt to reset the password and update the auth state based on the result
            val result = authRepository.resetPassword(email)
            _authState.value = if (result.isSuccess) AuthState.PasswordResetSuccess else AuthState.Error(result.exceptionOrNull()?.message ?: "Password reset failed")
        }
    }

    // Get the current user's name
    fun getCurrentUserName(): String? = authRepository.getCurrentUserName()

    // Validate the email format
    private fun isEmailValid(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Sign out the current user
    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Idle // Reset auth state to idle
        autoLogoutJob?.cancel() // Cancel the auto-logout job when logging out manually
    }

    companion object {
        private const val AUTO_LOGOUT_DELAY = 5 * 60 * 1000L // 5 minutes in milliseconds
    }
}

// Sealed class to represent the different authentication states
sealed class AuthState {
    data object Idle : AuthState() // Not authenticated
    data object Loading : AuthState() // Loading state during sign-in/sign-up
    data object Success : AuthState() // Successful authentication
    data object PasswordResetSuccess : AuthState() // Password reset successful
    data object AutoLoggedOut : AuthState() // User has been auto-logged out
    data class Error(val message: String) : AuthState() // Error state with message
}