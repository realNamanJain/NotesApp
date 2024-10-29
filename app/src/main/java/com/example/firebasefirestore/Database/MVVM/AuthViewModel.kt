package com.example.firebasefirestore.Database.MVVM

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun signUp(email: String, password: String) {
        if (!isEmailValid(email) || password.length < 6) {
            _authState.value = AuthState.Error("Invalid email or password.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUpWithEmailPassword(email, password)
            _authState.value = if (result.isSuccess) AuthState.Success else AuthState.Error(result.exceptionOrNull()?.message ?: "Sign-up failed")
        }
    }

    fun login(email: String, password: String) {
        if (!isEmailValid(email) || password.length < 6) {
            _authState.value = AuthState.Error("Invalid email or password.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithEmailPassword(email, password)
            _authState.value = if (result.isSuccess) AuthState.Success else AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
        }
    }

    fun resetPassword(email: String) {
        if (!isEmailValid(email)) {
            _authState.value = AuthState.Error("Please enter a valid email.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.resetPassword(email)
            _authState.value = if (result.isSuccess) AuthState.Success else AuthState.Error(result.exceptionOrNull()?.message ?: "Password reset failed")
        }
    }

    fun getCurrentUserName(): String? = authRepository.getCurrentUserName()

    private fun isEmailValid(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
}