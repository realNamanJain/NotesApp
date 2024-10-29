package com.example.firebasefirestore.Database.MemoMate

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Composable function to set up navigation for the app
@Composable
fun Navigation(auth: FirebaseAuth, firestore: FirebaseFirestore) {
    val navController = rememberNavController() // Create a NavController for navigation
    val repository = AuthRepository(auth) // Initialize the authentication repository
    val notesViewModel = MemoMateViewModel(MemoMateRepository(firestore)) // Initialize the notes ViewModel
    val viewModel = AuthViewModel(repository) // Initialize the authentication ViewModel

    // Set up navigation host
    NavHost(navController = navController, startDestination = "authScreen") {
        // Navigation route for authentication screen
        composable("authScreen") { AuthScreen(navController, viewModel) }
        // Navigation route for forgot password screen
        composable("forgotPassword") { ForgotPasswordScreen(navController, viewModel) }
        // Navigation route for the starting screen
        composable("memoMate_screen") { StartingScreen(viewModel, notesViewModel, repository, navController) }

        // Navigation route for memo writing screen
        composable("memo_UI") {
            MemoWritingScreen(notesViewModel, repository = repository, navController = navController)
        }

        // Navigation route for account screen
        composable("accountScreen") {
            AccountScreen(repository, navController)
        }

        // Navigation route for memo detail screen, taking title as a parameter
        composable("memo_detailScreen/{title}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") // Get title from arguments
            title?.let {
                // Navigate to the detail screen with the appropriate ViewModel and repository
                DetailScreen(
                    viewModel = notesViewModel,
                    repository = repository,
                    navController = navController,
                    title = it
                )
            }
        }
    }
}