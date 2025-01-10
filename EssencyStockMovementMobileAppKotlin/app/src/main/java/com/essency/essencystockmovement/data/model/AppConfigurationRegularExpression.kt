package com.essency.essencystockmovement.data.model

data class AppConfigurationRegularExpression(
    val id: Int = 0, // SQLite autoincrement
    val nameRegularExpression: String,
    val regularExpression: String
)