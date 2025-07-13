package com.example.simplenote.data.network

import com.example.simplenote.data.model.RefreshTokenRequest
import com.example.simplenote.data.model.TokenRefreshResponse
import com.example.simplenote.data.preferences.AuthPreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Named

@Singleton
class AuthRemoteDataSource @Inject constructor(
    @Named("refresh") private val refreshAuthApiService: AuthApiService,
    private val authPreferences: AuthPreferences
) {
    suspend fun refreshToken(): Boolean {
        val refreshToken = authPreferences.refreshToken.first()
        if (refreshToken != null) {
            val response = refreshAuthApiService.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful) {
                val refreshResponse: TokenRefreshResponse? = response.body()
                if (refreshResponse != null) {
                    // Update only access token, keep refresh token (assuming 1 hour expiry)
                    authPreferences.saveTokens(refreshResponse.access, refreshToken, 3600L)
                    return true
                }
            }
        }
        return false
    }
    
    suspend fun getAccessToken(): String? {
        return authPreferences.accessToken.first()
    }
} 