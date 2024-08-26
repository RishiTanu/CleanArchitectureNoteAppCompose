package com.example.cleanarchitecturenote.feature_note.domain.use_case

data class NoteUseCases(
    val getNoteUseCase: GetNotesUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val addNote: AddNoteUseCase,
    val getNoteIdUseCase: GetNoteIdUseCase
)
