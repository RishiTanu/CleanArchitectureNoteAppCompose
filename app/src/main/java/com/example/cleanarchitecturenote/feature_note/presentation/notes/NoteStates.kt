package com.example.cleanarchitecturenote.feature_note.presentation.notes

import com.example.cleanarchitecturenote.feature_note.domain.model.Note
import com.example.cleanarchitecturenote.feature_note.domain.util.NoteOrder
import com.example.cleanarchitecturenote.feature_note.domain.util.OrderType

data class NoteStates(
    val notes: List<Note> = emptyList(),
    val noteOrder : NoteOrder = NoteOrder.Date(OrderType.DESCENDING),
    val isOrderSectionVisible : Boolean = false
)
