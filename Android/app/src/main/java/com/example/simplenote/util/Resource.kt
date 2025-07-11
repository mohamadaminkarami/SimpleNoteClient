package com.example.simplenote.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}

sealed class AuthResult {
    data object Loading : AuthResult()
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class NoteResult {
    data object Loading : NoteResult()
    data object Success : NoteResult()
    data class Error(val message: String) : NoteResult()
} 