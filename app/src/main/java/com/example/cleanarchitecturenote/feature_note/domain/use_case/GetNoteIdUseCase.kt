package com.example.cleanarchitecturenote.feature_note.domain.use_case

import com.example.cleanarchitecturenote.feature_note.domain.model.Note
import com.example.cleanarchitecturenote.feature_note.domain.repository.NoteRepository


class GetNoteIdUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(id:Int) : Note?{
        return noteRepository.getNoteById(id)
    }
}