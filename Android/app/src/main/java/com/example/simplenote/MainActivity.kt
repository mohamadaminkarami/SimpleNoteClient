package com.example.simplenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simplenote.presentation.MainViewModel
import com.example.simplenote.presentation.auth.LoginScreen
import com.example.simplenote.presentation.auth.RegisterScreen
import com.example.simplenote.presentation.navigation.Screen
import com.example.simplenote.presentation.notes.NotesListScreen
import com.example.simplenote.ui.theme.SimpleNoteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleNoteTheme {
                SimpleNoteApp()
            }
        }
    }
}

@Composable
fun SimpleNoteApp(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val navController = rememberNavController()
    
    // Show loading screen while checking auth status
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        return
    }
    
    // Determine start destination based on auth status
    val startDestination = if (state.isLoggedIn) {
        Screen.NotesList.route
    } else {
        Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.NotesList.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        // Main app screens
        composable(Screen.NotesList.route) {
            NotesListScreen(
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.NotesList.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}