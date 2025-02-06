package com.essency.essencystockmovement.data.model

data class UserMovementData (
    val username: String,
    val name: String,
    val lastName: String,
    val isAdmin: Boolean,
    val type: String,
    val warehouse: String,
    val source: String,
    val destination: String
)