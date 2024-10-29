package com.example.firebasefirestore.Database.NotesApp

import android.content.Context
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AuthScreen(navController: NavController, firestore: FirebaseFirestore) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isLogin by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isLogin) "Login" else "Sign Up", style = TextStyle(fontSize = 24.sp))

        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val errorMsg = if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Please enter a valid email"
            else if (password.length < 6) "Password should be at least 6 characters" else null
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (isLogin) loginWithEmailPassword(navController, context, email, password)
            else signUpWithEmailPassword(navController, context, email, password)
        }) {
            Text(text = if (isLogin) "Login" else "Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { isLogin = !isLogin }) {
            Text(text = if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Login")
        }
        TextButton(onClick = { navController.navigate("forgotPassword") }) {
            Text(text = "Forgot Password")
        }
    }
}

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Reset Password", style = TextStyle(fontSize = 24.sp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            resetPassword(context, email) { result ->
                val message = if (result) "Password reset email sent" else "Error in sending reset email"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                if (result) navController.navigate("authScreen")
            }
        }) {
            Text(text = "Reset Password")
        }
    }
}

@Composable
fun homeScreen() {
    Text(text = "Home screen", style = TextStyle(fontSize = 24.sp))
}

@Composable
fun getCurrentUserName(): String? {
    val user = FirebaseAuth.getInstance().currentUser
    return user?.email?.let { email ->
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email.substringBefore("@")
        } else {
            null // or throw an exception if you prefer
        }
    }
}

// Function to reset password via email
fun resetPassword(context: Context, email: String, onResult: (Boolean) -> Unit) {
    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true)
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        }
}


fun signUpWithEmailPassword(navController: NavController, context: Context, email: String, password: String) {
    val auth = FirebaseAuth.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign up success
                val user = auth.currentUser
                Toast.makeText(context, "User created successfully", Toast.LENGTH_SHORT).show()
                navController.navigate("memoMate_screen")

            } else {
                // If sign up fails, display a message to the user.
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
fun loginWithEmailPassword(navController: NavController, context: Context, email: String, password: String) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success
                Toast.makeText(context, "Logged in successfully", Toast.LENGTH_SHORT).show()
                navController.navigate("memoMate_screen")
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun preview(){
//    AuthScreen(FirebaseFirestore.getInstance())
}