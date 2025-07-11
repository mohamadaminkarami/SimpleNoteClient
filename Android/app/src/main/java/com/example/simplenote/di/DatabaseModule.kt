package com.example.simplenote.di

import android.content.Context
import androidx.room.Room
import com.example.simplenote.data.local.SimpleNoteDatabase
import com.example.simplenote.data.local.NoteDao
import com.example.simplenote.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SimpleNoteDatabase {
        return Room.databaseBuilder(
            context,
            SimpleNoteDatabase::class.java,
            SimpleNoteDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
         .build()
    }
    
    @Provides
    fun provideNoteDao(database: SimpleNoteDatabase): NoteDao {
        return database.noteDao()
    }
    
    @Provides
    fun provideUserDao(database: SimpleNoteDatabase): UserDao {
        return database.userDao()
    }
} 