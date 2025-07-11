package com.example.simplenote.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.simplenote.data.model.Note
import com.example.simplenote.data.model.User

@Database(
    entities = [Note::class, User::class],
    version = 1,
    exportSchema = false
)
abstract class SimpleNoteDatabase : RoomDatabase() {
    
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao
    
    companion object {
        const val DATABASE_NAME = "simple_note_database"
    }
} 