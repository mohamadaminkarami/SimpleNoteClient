package com.example.simplenote.presentation.notes

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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {
    
    var state by mutableStateOf(NotesState())
        private set
    
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
                searchNotes(event.query)
            }
            is NotesEvent.RefreshNotes -> {
                refreshNotes()
            }
            is NotesEvent.DeleteNote -> {
                deleteNote(event.noteId)
            }
            is NotesEvent.ClearError -> {
                state = state.copy(error = null)
            }
            is NotesEvent.ClearSearchQuery -> {
                state = state.copy(searchQuery = "")
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
            }
        }
    }
    
    private fun loadNotes() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            
            when (val result = notesRepository.refreshNotes()) {
                is Resource.Success -> {
                    // Notes will be updated via observeNotes()
                    state = state.copy(isLoading = false)
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message
                    )
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
            
            when (val result = notesRepository.refreshNotes()) {
                is Resource.Success -> {
                    state = state.copy(isRefreshing = false)
                }
                is Resource.Error -> {
                    state = state.copy(
                        isRefreshing = false,
                        error = result.message
                    )
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
            }
        }
    }
    
    private fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            when (val result = notesRepository.deleteNote(noteId)) {
                is NoteResult.Success -> {
                    _uiEvent.emit(NotesUiEvent.NoteDeleted)
                }
                is NoteResult.Error -> {
                    state = state.copy(error = result.message)
                }
                is NoteResult.Loading -> {
                    // Handle if needed
                }
            }
        }
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
} 