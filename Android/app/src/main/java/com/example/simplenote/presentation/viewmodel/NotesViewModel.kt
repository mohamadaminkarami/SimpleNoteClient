package com.example.simplenote.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.model.Note
import com.example.simplenote.domain.repository.NotesRepository
import com.example.simplenote.util.NoteResult
import com.example.simplenote.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {
    
    var state by mutableStateOf(NotesState())
        private set
    
    private val _uiState = MutableStateFlow(NotesState())
    val uiState = _uiState.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<NotesUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    
    private var searchJob: Job? = null
    
    init {
        loadNotes()
        observeNotes()
    }
    
    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.SearchQueryChanged -> {
                state = state.copy(searchQuery = event.query)
                _uiState.value = state
                searchNotes(event.query)
            }
            is NotesEvent.RefreshNotes -> {
                refreshNotes()
            }
            is NotesEvent.DeleteNote -> {
                deleteNoteInternal(event.noteId)
            }
            is NotesEvent.ClearError -> {
                state = state.copy(error = null)
                _uiState.value = state
            }
            is NotesEvent.ClearSearchQuery -> {
                state = state.copy(searchQuery = "")
                _uiState.value = state
                observeNotes() // Show all notes again
            }
        }
    }
    
    private fun observeNotes() {
        viewModelScope.launch {
            notesRepository.getAllNotes().collect { notes ->
                state = state.copy(
                    notes = notes,
                    isLoading = false
                )
                _uiState.value = state
            }
        }
    }
    
    private fun loadNotes() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            _uiState.value = state
            
            when (val result = notesRepository.refreshNotes()) {
                is Resource.Success -> {
                    // Notes will be updated via observeNotes()
                    state = state.copy(isLoading = false)
                    _uiState.value = state
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message
                    )
                    _uiState.value = state
                }
                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }
    
    private fun refreshNotes() {
        viewModelScope.launch {
            state = state.copy(isRefreshing = true, error = null)
            _uiState.value = state
            
            when (val result = notesRepository.refreshNotes()) {
                is Resource.Success -> {
                    state = state.copy(isRefreshing = false)
                    _uiState.value = state
                }
                is Resource.Error -> {
                    state = state.copy(
                        isRefreshing = false,
                        error = result.message
                    )
                    _uiState.value = state
                }
                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }
    
    private fun searchNotes(query: String) {
        searchJob?.cancel()
        
        if (query.isBlank()) {
            observeNotes()
            return
        }
        
        searchJob = viewModelScope.launch {
            // Add small delay for better UX
            delay(300)
            
            notesRepository.searchNotes(query).collect { notes ->
                state = state.copy(
                    notes = notes,
                    isLoading = false
                )
                _uiState.value = state
            }
        }
    }
    
    private fun deleteNoteInternal(noteId: Int) {
        viewModelScope.launch {
            when (val result = notesRepository.deleteNote(noteId)) {
                is NoteResult.Success -> {
                    _uiEvent.emit(NotesUiEvent.NoteDeleted)
                }
                is NoteResult.Error -> {
                    state = state.copy(error = result.message)
                    _uiState.value = state
                }
                is NoteResult.Loading -> {
                    // Handle if needed
                }
            }
        }
    }
    
    // Public methods for NoteDetailScreen
    fun createNote(title: String, description: String) {
        viewModelScope.launch {
            when (val result = notesRepository.createNote(title, description)) {
                is NoteResult.Success -> {
                    _uiEvent.emit(NotesUiEvent.NoteCreated)
                }
                is NoteResult.Error -> {
                    state = state.copy(error = result.message)
                    _uiState.value = state
                }
                is NoteResult.Loading -> {
                    // Handle if needed
                }
            }
        }
    }
    
    fun updateNote(note: Note) {
        viewModelScope.launch {
            when (val result = notesRepository.updateNote(note.id, note.title, note.description)) {
                is NoteResult.Success -> {
                    _uiEvent.emit(NotesUiEvent.NoteUpdated)
                }
                is NoteResult.Error -> {
                    state = state.copy(error = result.message)
                    _uiState.value = state
                }
                is NoteResult.Loading -> {
                    // Handle if needed
                }
            }
        }
    }
    
    fun deleteNote(noteId: Int) {
        deleteNoteInternal(noteId)
    }
}

data class NotesState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

sealed class NotesEvent {
    data class SearchQueryChanged(val query: String) : NotesEvent()
    data object RefreshNotes : NotesEvent()
    data class DeleteNote(val noteId: Int) : NotesEvent()
    data object ClearError : NotesEvent()
    data object ClearSearchQuery : NotesEvent()
}

sealed class NotesUiEvent {
    data object NoteDeleted : NotesUiEvent()
    data object NoteCreated : NotesUiEvent()
    data object NoteUpdated : NotesUiEvent()
} 