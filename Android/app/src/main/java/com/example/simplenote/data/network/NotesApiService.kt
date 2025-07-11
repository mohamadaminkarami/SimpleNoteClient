package com.example.simplenote.data.network

import com.example.simplenote.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface NotesApiService {
    
    @GET("notes/")
    suspend fun getNotes(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<PaginatedResponse<Note>>
    
    @GET("notes/{id}/")
    suspend fun getNoteById(@Path("id") id: Int): Response<Note>
    
    @POST("notes/")
    suspend fun createNote(@Body request: NoteRequest): Response<Note>
    
    @PUT("notes/{id}/")
    suspend fun updateNote(
        @Path("id") id: Int,
        @Body request: NoteRequest
    ): Response<Note>
    
    @DELETE("notes/{id}/")
    suspend fun deleteNote(@Path("id") id: Int): Response<Unit>
    
    @GET("notes/filter")
    suspend fun searchNotes(
        @Query("title") title: String? = null,
        @Query("content") content: String? = null,
        @Query("description") description: String? = null
    ): Response<List<Note>>
    
    @POST("notes/bulk")
    suspend fun bulkCreateNotes(@Body notes: List<NoteRequest>): Response<PaginatedResponse<Note>>
} 