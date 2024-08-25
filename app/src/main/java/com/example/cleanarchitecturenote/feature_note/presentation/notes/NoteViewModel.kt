package com.example.cleanarchitecturenote.feature_note.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cleanarchitecturenote.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notesUseCases: NoteUseCases
) : ViewModel() {

    private val _state = mutableStateOf<NoteStates>(NoteStates())
    val state: State<NoteStates> = _state

    init {

    }

    fun onEvent(noteEvent: NotesEvent) {
        when (noteEvent) {
            is NotesEvent.Order -> {

            }

            is NotesEvent.DeleteNote -> {

            }

            is NotesEvent.RestoreNote -> {

            }

            is NotesEvent.ToggleOrderSection -> {
                _state.value = _state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }

        }
    }

}