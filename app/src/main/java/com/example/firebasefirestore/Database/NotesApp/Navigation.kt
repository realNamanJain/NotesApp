package com.example.firebasefirestore.Database.NotesApp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@Composable
fun MainNavHost(firestore: FirebaseFirestore) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "authScreen") {
        composable("memoMate_screen") {
            MemoMateScreen(navController = navController, firestore = firestore)
        }

        composable("memo_UI") {
            MemoUI(navController = navController,firestore = firestore)
        }

        composable("memo_detailScreen/{title}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")
            title?.let {
                MemoDetailScreen(navController = navController,firestore = firestore, title = it)
            }
        }

        composable("authScreen") {
            AuthScreen(navController = navController, firestore = firestore)
        }

        composable("forgotPassword") {
            ForgotPasswordScreen(navController = navController)
        }
    }
}
