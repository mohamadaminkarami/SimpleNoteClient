package com.example.simplenote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int = 0,
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null
) 