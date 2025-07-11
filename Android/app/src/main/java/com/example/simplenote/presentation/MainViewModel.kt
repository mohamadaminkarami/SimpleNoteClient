package com.example.simplenote.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.domain.repository.AuthRepository
import com.example.simplenote.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    var state by mutableStateOf(MainState())
        private set
    
    init {
        checkAuthStatus()
        checkOnboardingStatus()
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
    
    private fun checkOnboardingStatus() {
        state = state.copy(
            isOnboardingCompleted = preferencesManager.isOnboardingCompleted()
        )
    }
    
    fun completeOnboarding() {
        preferencesManager.setOnboardingCompleted()
        state = state.copy(
            isOnboardingCompleted = true
        )
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}

data class MainState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = true,
    val isOnboardingCompleted: Boolean = false
) 