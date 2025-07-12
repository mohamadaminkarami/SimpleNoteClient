package com.example.simplenote.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

@Serializable
data class TokenResponse(
    val access: String,
    val refresh: String
)

@Serializable
data class RefreshTokenRequest(
    val refresh: String
)

@Serializable
data class TokenRefreshResponse(
    val access: String
)

// Change Password API

@Serializable
data class ChangePasswordRequest(
    val old_password: String,
    val new_password: String
)

@Serializable
data class MessageResponse(
    val detail: String
) 