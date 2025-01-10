package com.essency.essencystockmovement.data.model

import java.time.LocalDateTime

data class LogEntry (
    val id: Int = 0, // SQLite autoincrement
    val timestamp: LocalDateTime,
    val logLevel: String,
    val message: String,
    val exception: String
)