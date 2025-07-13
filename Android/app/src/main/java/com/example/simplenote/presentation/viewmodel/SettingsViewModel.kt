package com.example.simplenote.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.model.User
import com.example.simplenote.data.network.AuthApiService
import com.example.simplenote.domain.repository.AuthRepository
import com.example.simplenote.util.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authApiService: AuthApiService,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun fetchUserInfo() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val response = authApiService.getUserInfo()
                if (response.isSuccessful) {
                    _uiState.value = SettingsUiState(user = response.body(), isLoading = false)
                } else {
                    _uiState.value = SettingsUiState(error = "Failed to load user info.", isLoading = false)
                }
            } catch (_: IOException) {
                _uiState.value = SettingsUiState(error = "Network error.", isLoading = false)
            } catch (_: HttpException) {
                _uiState.value = SettingsUiState(error = "Server error.", isLoading = false)
            } catch (e: Exception) {
                _uiState.value = SettingsUiState(error = e.localizedMessage ?: "Unknown error", isLoading = false)
            }
        }
    }
    
    fun refreshToken() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshingToken = true, error = null)
            when (val result = authRepository.refreshToken()) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isRefreshingToken = false)
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isRefreshingToken = false,
                        error = result.message
                    )
                }
                is AuthResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }
}

data class SettingsUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshingToken: Boolean = false
) 