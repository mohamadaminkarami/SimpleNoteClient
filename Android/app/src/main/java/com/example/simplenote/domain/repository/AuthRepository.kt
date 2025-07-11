package com.example.simplenote.domain.repository

import com.example.simplenote.data.model.User
import com.example.simplenote.util.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): AuthResult
    suspend fun register(username: String, email: String, password: String): AuthResult
    suspend fun logout()
    suspend fun refreshToken(): AuthResult
    fun getCurrentUser(): Flow<User?>
    fun isLoggedIn(): Flow<Boolean>
} 