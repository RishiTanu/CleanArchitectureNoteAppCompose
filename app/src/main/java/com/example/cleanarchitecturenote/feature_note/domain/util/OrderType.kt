package com.example.cleanarchitecturenote.feature_note.domain.util

sealed class OrderType {
    data object  ASCENDING : OrderType()
    data object  DESCENDING : OrderType()
}