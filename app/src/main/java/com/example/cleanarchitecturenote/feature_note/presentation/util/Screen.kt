package com.example.cleanarchitecturenote.feature_note.presentation.util

sealed class Screen(val route: String) {
    data object NoteScreen : Screen("notes_screen")
    data object AddEditNoteScreen : Screen("add_edit_note_screen")
}