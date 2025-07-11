package com.example.simplenote.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val focusManager = LocalFocusManager.current
    
    // Handle UI events
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.LoginSuccess -> {
                    onLoginSuccess()
                }
                is AuthUiEvent.RegisterSuccess -> {
                    // Not applicable for login screen
                }
            }
        }
    }
    
    // Show error snackbar
    if (state.error != null) {
        LaunchedEffect(state.error) {
            // Auto-clear error after showing
            kotlinx.coroutines.delay(3000)
            viewModel.onEvent(AuthEvent.ClearError)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title
        Text(
            text = "SimpleNote",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Username Field
        OutlinedTextField(
            value = state.username,
            onValueChange = { viewModel.onEvent(AuthEvent.UsernameChanged(it)) },
            label = { Text("Username") },
            placeholder = { Text("Enter your username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password Field
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(AuthEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading,
            visualTransformation = if (state.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = { viewModel.onEvent(AuthEvent.TogglePasswordVisibility) }
                ) {
                    Icon(
                        imageVector = if (state.isPasswordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (state.isPasswordVisible) {
                            "Hide password"
                        } else {
                            "Show password"
                        }
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { 
                    focusManager.clearFocus()
                    viewModel.onEvent(AuthEvent.Login)
                }
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Login Button
        Button(
            onClick = { viewModel.onEvent(AuthEvent.Login) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Log In")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Register Link
        TextButton(
            onClick = onNavigateToRegister,
            enabled = !state.isLoading
        ) {
            Text("Don't have an account? Sign up")
        }
        
        // Error Message
        if (state.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = state.error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
} 