package com.example.simplenote.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.simplenote.ui.theme.LoginTextPrimary
import com.example.simplenote.ui.theme.LoginTextSecondary
import com.example.simplenote.ui.theme.LoginPlaceholder
import com.example.simplenote.ui.theme.OnboardingPurple

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
        // Main Title
        Text(
            text = "Let's Login",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold, // 700
                lineHeight = (32 * 1.2).sp, // 120% line height
                letterSpacing = 0.sp
            ),
            color = LoginTextPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "And notes your idea",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal, // 400
                lineHeight = (16 * 1.4).sp, // 140% line height
                letterSpacing = 0.sp
            ),
            color = LoginTextSecondary,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Username Field
        OutlinedTextField(
            value = state.username,
            onValueChange = { viewModel.onEvent(AuthEvent.UsernameChanged(it)) },
            label = {
                Text(
                    text = "Username",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium, // 500
                        lineHeight = (16 * 1.4).sp, // 140% line height
                        letterSpacing = 0.sp
                    ),
                    color = LoginTextPrimary
                )
            },
            placeholder = {
                Text(
                    text = "Example: johndoe",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal, // 400
                        lineHeight = (16 * 1.4).sp, // 140% line height
                        letterSpacing = 0.sp
                    ),
                    color = LoginPlaceholder
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading,
            shape = RoundedCornerShape(12.dp),
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
            label = {
                Text(
                    text = "Password",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium, // 500
                        lineHeight = (16 * 1.4).sp, // 140% line height
                        letterSpacing = 0.sp
                    ),
                    color = LoginTextPrimary
                )
            },
            placeholder = {
                Text(
                    text = "••••••••",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal, // 400
                        lineHeight = (16 * 1.4).sp, // 140% line height
                        letterSpacing = 0.sp
                    ),
                    color = LoginPlaceholder
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading,
            shape = RoundedCornerShape(12.dp),
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

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = { viewModel.onEvent(AuthEvent.Login) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(100.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = OnboardingPurple,
                contentColor = Color.White
            )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(
                text = "Or",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Divider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register Link
        TextButton(
            onClick = onNavigateToRegister,
            enabled = !state.isLoading
        ) {
            Text(
                text = "Don't have any account? Register here",
                color = OnboardingPurple,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                ),
            )
        }

        // Error Message
        if (state.error != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
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