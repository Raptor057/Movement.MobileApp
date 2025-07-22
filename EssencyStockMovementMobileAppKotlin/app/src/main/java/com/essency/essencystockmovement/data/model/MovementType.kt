package com.essency.essencystockmovement.data.model

data class MovementType(
    val id: Int,         // ID del movimiento
    val usertype: String, // Tipo de usuario
    val type: String,    // Tipo de movimiento
    val source: String,  // Origen del movimiento
    val destination: String // Destino del movimiento
)

