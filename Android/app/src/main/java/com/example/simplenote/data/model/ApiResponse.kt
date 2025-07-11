package com.example.simplenote.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

@Serializable
data class NoteRequest(
    val title: String,
    val description: String
)

@Serializable
data class ApiError(
    val type: String,
    val errors: List<ErrorDetail>
)

@Serializable
data class ErrorDetail(
    val code: String,
    val detail: String,
    val attr: String?
) 