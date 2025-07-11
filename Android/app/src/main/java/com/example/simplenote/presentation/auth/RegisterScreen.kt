package com.example.simplenote.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val focusManager = LocalFocusManager.current
    
    // Handle UI events
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.RegisterSuccess -> {
                    onRegisterSuccess()
                }
                is AuthUiEvent.LoginSuccess -> {
                    // Not applicable for register screen
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
        // Back button and title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                enabled = !state.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Join SimpleNote today!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Username Field
        OutlinedTextField(
            value = state.username,
            onValueChange = { viewModel.onEvent(AuthEvent.UsernameChanged(it)) },
            label = { Text("Username") },
            placeholder = { Text("Choose a username") },
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
        
        // Email Field
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(AuthEvent.EmailChanged(it)) },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
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
            placeholder = { Text("Create a password") },
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
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Confirm Password Field
        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { viewModel.onEvent(AuthEvent.ConfirmPasswordChanged(it)) },
            label = { Text("Confirm Password") },
            placeholder = { Text("Confirm your password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading,
            visualTransformation = if (state.isConfirmPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = { viewModel.onEvent(AuthEvent.ToggleConfirmPasswordVisibility) }
                ) {
                    Icon(
                        imageVector = if (state.isConfirmPasswordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (state.isConfirmPasswordVisible) {
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
                    viewModel.onEvent(AuthEvent.Register)
                }
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Password hint
        Text(
            text = "Password must be at least 8 characters",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Register Button
        Button(
            onClick = { viewModel.onEvent(AuthEvent.Register) },
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
                Text("Sign Up")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Login Link
        TextButton(
            onClick = onNavigateBack,
            enabled = !state.isLoading
        ) {
            Text("Already have an account? Log in")
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