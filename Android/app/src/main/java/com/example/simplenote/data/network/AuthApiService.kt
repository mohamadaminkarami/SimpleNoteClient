package com.example.simplenote.data.network

import com.example.simplenote.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    
    @POST("auth/register/")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>
    
    @POST("auth/token/")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>
    
    @POST("auth/token/refresh/")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<TokenRefreshResponse>
    
    @GET("auth/userinfo/")
    suspend fun getUserInfo(): Response<User>

    @POST("auth/change-password/")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<MessageResponse>
} 