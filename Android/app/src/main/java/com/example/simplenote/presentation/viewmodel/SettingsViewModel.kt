package com.example.simplenote.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.model.User
import com.example.simplenote.data.network.AuthApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authApiService: AuthApiService
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
            } catch (e: IOException) {
                _uiState.value = SettingsUiState(error = "Network error.", isLoading = false)
            } catch (e: HttpException) {
                _uiState.value = SettingsUiState(error = "Server error.", isLoading = false)
            } catch (e: Exception) {
                _uiState.value = SettingsUiState(error = e.localizedMessage ?: "Unknown error", isLoading = false)
            }
        }
    }
}

data class SettingsUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) 