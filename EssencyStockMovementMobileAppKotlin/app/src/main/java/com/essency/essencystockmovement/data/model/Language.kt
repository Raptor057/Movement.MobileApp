package com.essency.essencystockmovement.data.model

data class Language(
    val id: Int,              // ID del idioma
    val languageName: String, // Nombre del idioma
    val isActive: Boolean     // Indica si el idioma está activo
)