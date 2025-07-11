package com.example.simplenote.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object NotesList : Screen("notes_list")
    data object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Int) = "note_detail/$noteId"
    }
    data object NoteEditor : Screen("note_editor?noteId={noteId}") {
        fun createRoute(noteId: Int? = null) = if (noteId != null) {
            "note_editor?noteId=$noteId"
        } else {
            "note_editor"
        }
    }
    data object Profile : Screen("profile")
} 