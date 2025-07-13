package com.example.simplenote.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.model.ChangePasswordRequest
import com.example.simplenote.data.network.AuthApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authApiService: AuthApiService
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        retypePassword: String
    ) {
        if (oldPassword.isBlank() || newPassword.isBlank() || retypePassword.isBlank()) {
            _uiState.value = ChangePasswordUiState(error = "All fields are required.")
            return
        }
        if (newPassword != retypePassword) {
            _uiState.value = ChangePasswordUiState(error = "New passwords do not match.")
            return
        }
        // Optionally: add more password validation here
        _uiState.value = ChangePasswordUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val response = authApiService.changePassword(
                    ChangePasswordRequest(
                        old_password = oldPassword,
                        new_password = newPassword
                    )
                )
                if (response.isSuccessful) {
                    _uiState.value = ChangePasswordUiState(success = response.body()?.detail ?: "Password changed successfully.")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    _uiState.value = ChangePasswordUiState(error = errorMsg)
                }
            } catch (_: IOException) {
                _uiState.value = ChangePasswordUiState(error = "Network error. Please try again.")
            } catch (_: HttpException) {
                _uiState.value = ChangePasswordUiState(error = "Server error. Please try again.")
            } catch (e: Exception) {
                _uiState.value = ChangePasswordUiState(error = e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = null)
    }
}

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null
) 