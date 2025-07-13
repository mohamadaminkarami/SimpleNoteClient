package com.example.simplenote.data.repository

import com.example.simplenote.data.local.NoteDao
import com.example.simplenote.data.model.Note
import com.example.simplenote.data.model.NoteRequest
import com.example.simplenote.data.network.NotesApiService
import com.example.simplenote.data.network.TokenManager
import com.example.simplenote.domain.repository.NotesRepository
import com.example.simplenote.util.NoteResult
import com.example.simplenote.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val notesApiService: NotesApiService,
    private val noteDao: NoteDao,
    private val tokenManager: TokenManager
) : NotesRepository {
    
    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes()
    }
    
    override suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }
    
    override suspend fun createNote(title: String, content: String): NoteResult {
        return try {
            // First, try to create on server
            val response = notesApiService.createNote(NoteRequest(title, content))
            
            if (response.isSuccessful) {
                val note = response.body()
                if (note != null) {
                    // Save to local database
                    noteDao.insertNote(note)
                    NoteResult.Success
                } else {
                    NoteResult.Error("Failed to create note: Empty response")
                }
            } else {
                // If server fails, create locally with temporary ID
                // This will be synced later when network is available
                val tempNote = Note(
                    id = 0, // Room will auto-generate
                    title = title,
                    description = content,
                    createdAt = getCurrentTimestamp(),
                    updatedAt = getCurrentTimestamp()
                )
                noteDao.insertNote(tempNote)
                NoteResult.Success
            }
        } catch (e: Exception) {
            // Network error - save locally
            val tempNote = Note(
                id = 0,
                title = title,
                description = content,
                createdAt = getCurrentTimestamp(),
                updatedAt = getCurrentTimestamp()
            )
            noteDao.insertNote(tempNote)
            NoteResult.Success
        }
    }
    
    override suspend fun updateNote(id: Int, title: String, content: String): NoteResult {
        return try {
            val response = notesApiService.updateNote(id, NoteRequest(title, content))
            
            if (response.isSuccessful) {
                val note = response.body()
                if (note != null) {
                    noteDao.insertNote(note) // Insert or replace
                    NoteResult.Success
                } else {
                    NoteResult.Error("Failed to update note")
                }
            } else {
                // Update locally if server fails
                val existingNote = noteDao.getNoteById(id)
                if (existingNote != null) {
                    val updatedNote = existingNote.copy(
                        title = title,
                        description = content,
                        updatedAt = getCurrentTimestamp()
                    )
                    noteDao.updateNote(updatedNote)
                    NoteResult.Success
                } else {
                    NoteResult.Error("Note not found")
                }
            }
        } catch (e: Exception) {
            // Update locally if network error
            val existingNote = noteDao.getNoteById(id)
            if (existingNote != null) {
                val updatedNote = existingNote.copy(
                    title = title,
                    description = content,
                    updatedAt = getCurrentTimestamp()
                )
                noteDao.updateNote(updatedNote)
                NoteResult.Success
            } else {
                NoteResult.Error("Note not found locally")
            }
        }
    }
    
    override suspend fun deleteNote(id: Int): NoteResult {
        return try {
            val response = notesApiService.deleteNote(id)
            
            if (response.isSuccessful) {
                noteDao.deleteNoteById(id)
                NoteResult.Success
            } else {
                // Mark for deletion locally
                noteDao.deleteNoteById(id)
                NoteResult.Success
            }
        } catch (e: Exception) {
            // Delete locally if network error
            noteDao.deleteNoteById(id)
            NoteResult.Success
        }
    }
    
    override fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query)
    }
    
    override suspend fun syncNotes(): Resource<Unit> {
        return try {
            // This would implement complex sync logic
            // For now, just refresh from server
            refreshNotes()
        } catch (e: Exception) {
            Resource.Error("Sync failed: ${e.localizedMessage}")
        }
    }
    
    override suspend fun refreshNotes(): Resource<Unit> {
        return try {
            // Ensure we have a valid token before making the request
            if (!tokenManager.ensureValidToken()) {
                return Resource.Error("Authentication failed")
            }
            
            val response = notesApiService.getNotes()
            
            if (response.isSuccessful) {
                val notesResponse = response.body()
                if (notesResponse != null) {
                    // Clear and insert all notes
                    noteDao.deleteAllNotes()
                    noteDao.insertNotes(notesResponse.results)
                    Resource.Success(Unit)
                } else {
                    Resource.Error("Empty response from server")
                }
            } else {
                Resource.Error("Failed to refresh notes: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }
    
    private fun getCurrentTimestamp(): String {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }
} 