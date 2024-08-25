package com.example.cleanarchitecturenote.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cleanarchitecturenote.ui.theme.Pink40
import com.example.cleanarchitecturenote.ui.theme.Purple40
import com.example.cleanarchitecturenote.ui.theme.PurpleGrey40


@Entity
data class Note(
    val title: String,
    val content: String,
    val timestamp: Long,
    val color: Int,
    @PrimaryKey val id: Int? = null,

    ) {
    companion object {
        val noteColors = listOf(Purple40, PurpleGrey40, Pink40)
    }
}

class InvalidNoteException(message:String) : Exception(message)