package com.example.cleanarchitecturenote.feature_note.presentation.notes.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cleanarchitecturenote.feature_note.domain.util.NoteOrder
import com.example.cleanarchitecturenote.feature_note.domain.util.OrderType

@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    noteOrder: NoteOrder = NoteOrder.Date(OrderType.DESCENDING),
    onOrderChange: (NoteOrder) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultRadioButtons(
                text = "Title",
                onSelected = noteOrder is NoteOrder.Title,
                onSelect = {
                    onOrderChange(NoteOrder.Title(noteOrder.orderType))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButtons(
                text = "Date",
                onSelected = noteOrder is NoteOrder.Date,
                onSelect = {
                    onOrderChange(NoteOrder.Date(noteOrder.orderType))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButtons(
                text = "Color",
                onSelected = noteOrder is NoteOrder.Color,
                onSelect = {
                    onOrderChange(NoteOrder.Color(noteOrder.orderType))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                DefaultRadioButtons(
                    text = "Ascending",
                    onSelected = noteOrder.orderType is OrderType.ASCENDING,
                    onSelect = {
                        onOrderChange(noteOrder.copy(OrderType.ASCENDING))
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                DefaultRadioButtons(
                    text = "Descending",
                    onSelected = noteOrder.orderType is OrderType.DESCENDING,
                    onSelect = {
                        onOrderChange(noteOrder.copy(OrderType.DESCENDING))
                    }
                )
            }

        }
    }

}