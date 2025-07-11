package com.example.simplenote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: Int = 0,
    val title: String,
    val description: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("creator_name") val creatorName: String? = null,
    @SerialName("creator_username") val creatorUsername: String? = null
) 