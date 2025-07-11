package com.example.simplenote.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.domain.repository.AuthRepository
import com.example.simplenote.util.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    var state by mutableStateOf(AuthState())
        private set
    
    private val _uiEvent = MutableSharedFlow<AuthUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    
    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.FirstNameChanged -> {
                state = state.copy(firstName = event.firstName)
            }
            is AuthEvent.LastNameChanged -> {
                state = state.copy(lastName = event.lastName)
            }
            is AuthEvent.UsernameChanged -> {
                state = state.copy(username = event.username)
            }
            is AuthEvent.EmailChanged -> {
                state = state.copy(email = event.email)
            }
            is AuthEvent.PasswordChanged -> {
                state = state.copy(password = event.password)
            }
            is AuthEvent.ConfirmPasswordChanged -> {
                state = state.copy(confirmPassword = event.confirmPassword)
            }
            is AuthEvent.Login -> {
                login()
            }
            is AuthEvent.Register -> {
                register()
            }
            is AuthEvent.TogglePasswordVisibility -> {
                state = state.copy(isPasswordVisible = !state.isPasswordVisible)
            }
            is AuthEvent.ToggleConfirmPasswordVisibility -> {
                state = state.copy(isConfirmPasswordVisible = !state.isConfirmPasswordVisible)
            }
            is AuthEvent.ClearError -> {
                state = state.copy(error = null)
            }
        }
    }
    
    private fun login() {
        if (!validateLoginInput()) return
        
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            
            when (val result = authRepository.login(state.email, state.password)) {
                is AuthResult.Success -> {
                    state = state.copy(isLoading = false)
                    _uiEvent.emit(AuthUiEvent.LoginSuccess)
                }
                is AuthResult.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is AuthResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }
    
    private fun register() {
        if (!validateRegisterInput()) return
        
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            
            when (val result = authRepository.register(state.firstName, state.lastName, state.username, state.email, state.password)) {
                is AuthResult.Success -> {
                    state = state.copy(isLoading = false)
                    _uiEvent.emit(AuthUiEvent.RegisterSuccess)
                }
                is AuthResult.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is AuthResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }
    
    private fun validateLoginInput(): Boolean {
        if (state.email.isBlank()) {
            state = state.copy(error = "Email cannot be empty")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            state = state.copy(error = "Invalid email format")
            return false
        }
        if (state.password.isBlank()) {
            state = state.copy(error = "Password cannot be empty")
            return false
        }
        return true
    }
    
    private fun validateRegisterInput(): Boolean {
        if (state.firstName.isBlank()) {
            state = state.copy(error = "First name cannot be empty")
            return false
        }
        if (state.lastName.isBlank()) {
            state = state.copy(error = "Last name cannot be empty")
            return false
        }
        if (state.username.isBlank()) {
            state = state.copy(error = "Username cannot be empty")
            return false
        }
        if (state.email.isBlank()) {
            state = state.copy(error = "Email cannot be empty")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            state = state.copy(error = "Invalid email format")
            return false
        }
        if (state.password.isBlank()) {
            state = state.copy(error = "Password cannot be empty")
            return false
        }
        if (state.password.length < 8) {
            state = state.copy(error = "Password must be at least 8 characters")
            return false
        }
        if (state.password != state.confirmPassword) {
            state = state.copy(error = "Passwords do not match")
            return false
        }
        return true
    }
}

data class AuthState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)

sealed class AuthEvent {
    data class FirstNameChanged(val firstName: String) : AuthEvent()
    data class LastNameChanged(val lastName: String) : AuthEvent()
    data class UsernameChanged(val username: String) : AuthEvent()
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : AuthEvent()
    data object Login : AuthEvent()
    data object Register : AuthEvent()
    data object TogglePasswordVisibility : AuthEvent()
    data object ToggleConfirmPasswordVisibility : AuthEvent()
    data object ClearError : AuthEvent()
}

sealed class AuthUiEvent {
    data object LoginSuccess : AuthUiEvent()
    data object RegisterSuccess : AuthUiEvent()
} 