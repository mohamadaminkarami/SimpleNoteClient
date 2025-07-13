package com.example.simplenote.data.network

import com.example.simplenote.data.preferences.AuthPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val authRemoteDataSource: AuthRemoteDataSource
) {
    private var refreshJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    
    fun startTokenRefreshMonitoring() {
        refreshJob?.cancel()
        refreshJob = scope.launch {
            while (true) {
                try {
                    if (authPreferences.shouldRefreshToken()) {
                        val refreshSuccess = authRemoteDataSource.refreshToken()
                        if (!refreshSuccess) {
                            break
                        }
                    }
                    delay(60000)
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(60000)
                }
            }
        }
    }
    
    fun stopTokenRefreshMonitoring() {
        refreshJob?.cancel()
        refreshJob = null
    }
    
    suspend fun ensureValidToken(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (authPreferences.shouldRefreshToken()) {
                    return@withContext authRemoteDataSource.refreshToken()
                }
                return@withContext true
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }
    }
    
    suspend fun isTokenValid(): Boolean {
        return !authPreferences.isTokenExpired()
    }
} 