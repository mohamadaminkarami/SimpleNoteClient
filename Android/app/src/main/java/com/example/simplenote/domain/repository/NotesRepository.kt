package com.example.simplenote.domain.repository

import com.example.simplenote.data.model.Note
import com.example.simplenote.util.NoteResult
import com.example.simplenote.util.Resource
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Int): Note?
    suspend fun createNote(title: String, content: String): NoteResult
    suspend fun updateNote(id: Int, title: String, content: String): NoteResult
    suspend fun deleteNote(id: Int): NoteResult
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun syncNotes(): Resource<Unit>
    suspend fun refreshNotes(): Resource<Unit>
} 