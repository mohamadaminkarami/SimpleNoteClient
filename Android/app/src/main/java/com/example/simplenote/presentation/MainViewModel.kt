package com.example.simplenote.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    var state by mutableStateOf(MainState())
        private set
    
    init {
        checkAuthStatus()
    }
    
    private fun checkAuthStatus() {
        viewModelScope.launch {
            authRepository.isLoggedIn().collect { isLoggedIn ->
                state = state.copy(
                    isLoggedIn = isLoggedIn,
                    isLoading = false
                )
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}

data class MainState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = true
) 