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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.simplenote.presentation.MainViewModel
import com.example.simplenote.presentation.auth.LoginScreen
import com.example.simplenote.presentation.auth.RegisterScreen
import com.example.simplenote.presentation.navigation.Screen
import com.example.simplenote.presentation.notes.NotesListScreen
import com.example.simplenote.presentation.notes.NoteDetailScreen
import com.example.simplenote.presentation.onboarding.OnboardingScreen
import com.example.simplenote.data.preferences.PreferencesManager
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
    
    // Determine start destination based on onboarding completion and auth status
    val startDestination = when {
        !state.isOnboardingCompleted -> Screen.Onboarding.route
        state.isLoggedIn -> Screen.NotesList.route
        else -> Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding screen
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStartedClick = {
                    // Mark onboarding as completed
                    viewModel.completeOnboarding()
                    
                    // Navigate to appropriate screen
                    val nextDestination = if (state.isLoggedIn) {
                        Screen.NotesList.route
                    } else {
                        Screen.Login.route
                    }
                    navController.navigate(nextDestination) {
                        popUpTo(Screen.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
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
                onNavigateToNoteDetail = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                },
                onNavigateToNoteEditor = { noteId ->
                    navController.navigate(Screen.NoteEditor.createRoute(noteId))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        
        // Combined Note Detail/Editor screen
        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(
                navArgument("noteId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")?.toString()
            NoteDetailScreen(
                noteId = noteId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
           route = Screen.NoteEditor.route,
            arguments = listOf(
                navArgument("noteId") { 
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")?.takeIf { it != -1 }?.toString()
            NoteDetailScreen(
                noteId = noteId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Profile.route) {
            // TODO: Implement ProfileScreen with proper logout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Profile Screen",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.NotesList.route) {
                                inclusive = true
                            }
                        }
                    }
                ) {
                    Text("Logout")
                }
            }
        }
    }
}