package com.essency.essencystockmovement.data.model

import java.time.LocalDateTime

data class TraceabilityStockList(
    val id: Int = 0, // SQLite autoincrement
    val idStock: Int,
    val saved: Boolean,
    val sendByEmail: Boolean,
    val timeStamp: LocalDateTime
)