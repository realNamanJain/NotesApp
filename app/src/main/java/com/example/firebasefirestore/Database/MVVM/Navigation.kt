package com.example.firebasefirestore.Database.MVVM

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Navigation(auth: FirebaseAuth) {
    val navController = rememberNavController()
    val repository = AuthRepository(auth)
    val notesViewModel = NotesViewModel(NotesRepository(FirebaseFirestore.getInstance())) // Remove 'val' here
    val viewModel = AuthViewModel(AuthRepository(auth))

    NavHost(navController = navController, startDestination = "authScreen") {
        composable("authScreen") { AuthScreen(navController, viewModel) }
        composable("forgotPassword") { ForgotPasswordScreen(navController, viewModel) }
//        composable("memoMate_screen") { MemoMateScreen(notesViewModel, repository, navController) }
    }
}

