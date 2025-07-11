package com.example.simplenote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: Int = 0,
    val title: String,
    val content: String,
    val created: String,
    val updated: String
) 