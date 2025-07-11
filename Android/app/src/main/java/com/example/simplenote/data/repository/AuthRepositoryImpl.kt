package com.example.simplenote.data.repository

import com.example.simplenote.data.local.UserDao
import com.example.simplenote.data.model.*
import com.example.simplenote.data.network.AuthApiService
import com.example.simplenote.data.preferences.AuthPreferences
import com.example.simplenote.domain.repository.AuthRepository
import com.example.simplenote.util.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val authPreferences: AuthPreferences,
    private val userDao: UserDao
) : AuthRepository {
    
    override suspend fun login(username: String, password: String): AuthResult {
        return try {
            val response = authApiService.login(LoginRequest(username, password))
            
            if (response.isSuccessful) {
                val tokenResponse = response.body()
                if (tokenResponse != null) {
                    // Save tokens
                    authPreferences.saveTokens(tokenResponse.access, tokenResponse.refresh)
                    authPreferences.saveUsername(username)
                    
                    // Fetch and save user info
                    fetchAndSaveUserInfo()
                    
                    AuthResult.Success
                } else {
                    AuthResult.Error("Login failed: Empty response")
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Invalid username or password"
                    422 -> "Invalid input data"
                    else -> "Login failed: ${response.message()}"
                }
                AuthResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            AuthResult.Error("Network error: ${e.localizedMessage}")
        }
    }
    
    override suspend fun register(username: String, email: String, password: String): AuthResult {
        return try {
            val response = authApiService.register(
                RegisterRequest(username, email, password)
            )
            
            if (response.isSuccessful) {
                AuthResult.Success
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Registration failed: Invalid data"
                    409 -> "Username or email already exists"
                    else -> "Registration failed: ${response.message()}"
                }
                AuthResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            AuthResult.Error("Network error: ${e.localizedMessage}")
        }
    }
    
    override suspend fun logout() {
        authPreferences.clearTokens()
        userDao.deleteAllUsers()
    }
    
    override suspend fun refreshToken(): AuthResult {
        return try {
            val refreshToken = authPreferences.refreshToken.first()
            if (refreshToken != null) {
                val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))
                
                if (response.isSuccessful) {
                    val refreshResponse = response.body()
                    if (refreshResponse != null) {
                        // Update only access token, keep refresh token
                        authPreferences.saveTokens(refreshResponse.access, refreshToken)
                        AuthResult.Success
                    } else {
                        AuthResult.Error("Token refresh failed: Empty response")
                    }
                } else {
                    AuthResult.Error("Token refresh failed: ${response.message()}")
                }
            } else {
                AuthResult.Error("No refresh token available")
            }
        } catch (e: Exception) {
            AuthResult.Error("Token refresh failed: ${e.localizedMessage}")
        }
    }
    
    override fun getCurrentUser(): Flow<User?> {
        return userDao.getCurrentUserFlow()
    }
    
    override fun isLoggedIn(): Flow<Boolean> {
        return authPreferences.accessToken.map { token ->
            !token.isNullOrEmpty()
        }
    }
    
    private suspend fun fetchAndSaveUserInfo() {
        try {
            val response = authApiService.getUserInfo()
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    userDao.insertUser(user)
                }
            }
        } catch (e: Exception) {
            // Silently fail - user info can be fetched later
        }
    }
} 