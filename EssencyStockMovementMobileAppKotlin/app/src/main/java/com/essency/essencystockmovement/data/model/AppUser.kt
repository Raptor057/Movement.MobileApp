package com.essency.essencystockmovement.data.model

import java.time.LocalDateTime

// AppUser.kt
data class AppUser(
    val id: Int = 0,             // SQLite autoincrement
    val userName: String,
    val name: String,
    val lastName: String,
    val passwordHash: String,
    val salt: String,
    val createUserDate: String,  // Podría ser String o un tipo de fecha
    val isAdmin: Boolean,
    val enable: Boolean
)
