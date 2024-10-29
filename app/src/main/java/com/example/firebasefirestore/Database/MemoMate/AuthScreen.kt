package com.example.firebasefirestore.Database.MemoMate

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

// Main authentication screen for Login and Sign Up
@Composable
fun AuthScreen(navController: NavController, viewModel: AuthViewModel) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") } // Track email input
    var password by rememberSaveable { mutableStateOf("") } // Track password input
    var isLogin by rememberSaveable { mutableStateOf(true) } // Toggle between Login and Sign Up

    // Observe authentication state from ViewModel
    val authState by viewModel.authState.observeAsState(initial = AuthState.Idle)

    // Handle changes in authState (success, error, etc.)
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> { // On successful login/signup
                Toast.makeText(context, "Operation successful", Toast.LENGTH_SHORT).show()
                navController.navigate("memoMate_screen") {
                    popUpTo("authScreen") { inclusive = true } // Navigate and clear backstack
                }
            }
            is AuthState.Error -> { // On error, show error message
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    // Main UI layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7874A)) // Background color
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display title (Login or Sign Up) based on isLogin value
            Text(
                text = if (isLogin) "Login" else "Sign Up",
                fontSize = 24.sp,
                style = TextStyle(fontFamily = FontFamily.Serif),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Show loading indicator if in loading state
            if (authState is AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                // Input field for email
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", style = TextStyle(fontFamily = FontFamily.Serif)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black)
                        .background(Color(0xFFF7874A)),
                    textStyle = TextStyle(fontFamily = FontFamily.Serif)
                )
                Spacer(modifier = Modifier.height(16.dp)) // Space between Email and Password fields

                // Input field for password
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", style = TextStyle(fontFamily = FontFamily.Serif)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black)
                        .background(Color(0xFFF7874A)),
                    visualTransformation = PasswordVisualTransformation(), // Hide password text
                    textStyle = TextStyle(fontFamily = FontFamily.Serif)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Button to submit login or sign up request
                Button(onClick = {
                    if (isLogin) viewModel.login(email, password)
                    else viewModel.signUp(email, password)
                }) {
                    Text(text = if (isLogin) "Login" else "Sign Up", style = TextStyle(fontFamily = FontFamily.Serif), color = Color.Black)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Toggle between login and sign-up
                TextButton(onClick = { isLogin = !isLogin }) {
                    Text(
                        text = if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Login",
                        style = TextStyle(fontFamily = FontFamily.Serif), color = Color.Black
                    )
                }

                // Navigate to forgot password screen
                TextButton(onClick = { navController.navigate("forgotPassword") }) {
                    Text(text = "Forgot Password", style = TextStyle(fontFamily = FontFamily.Serif), color = Color.Black)
                }
            }
        }
    }
}

// Forgot Password screen for sending a password reset email
@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: AuthViewModel) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") } // Track email input

    // Observe authentication state from ViewModel
    val authState by viewModel.authState.observeAsState(initial = AuthState.Idle)

    // Handle changes in authState (success, error, etc.)
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.PasswordResetSuccess -> { // On successful reset email sent
                Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
                navController.navigate("authScreen") {
                    popUpTo("forgotPassword") { inclusive = true } // Navigate and clear backstack
                }
            }
            is AuthState.Error -> { // On error, show error message
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {} // Handle other states as necessary
        }
    }

    // UI layout for Forgot Password screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF7874A)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Reset Password", fontSize = 24.sp, style = TextStyle(fontFamily = FontFamily.Serif), color = Color.Black)

        // Input field for email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7874A))
                .border(1.dp, Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Button to send password reset email
        Button(onClick = { viewModel.resetPassword(email) }) {
            Text(text = "Reset Password", color = Color.Black)
        }
    }
}