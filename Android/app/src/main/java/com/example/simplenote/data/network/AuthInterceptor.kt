package com.example.simplenote.data.network

import com.example.simplenote.data.preferences.AuthPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val authRemoteDataSource: AuthRemoteDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath
        val skipAuth = path.contains("/auth/token/") ||
                       path.contains("/auth/register/")
        if (skipAuth) {
            return chain.proceed(originalRequest)
        }
        val accessToken = runBlocking {
            authRemoteDataSource.getAccessToken()
        }
        val authenticatedRequest = if (accessToken != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }
        var response = chain.proceed(authenticatedRequest)
        if (response.code == 401 && accessToken != null) {
            response.close()
            val refreshSuccess = runBlocking {
                authRemoteDataSource.refreshToken()
            }
            if (refreshSuccess) {
                val newAccessToken = runBlocking {
                    authRemoteDataSource.getAccessToken()
                }
                if (newAccessToken != null) {
                    val retryRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                    return chain.proceed(retryRequest)
                }
            }
        }
        return response
    }
} 