package com.essency.essencystockmovement.data.model

data class TraceabilityStockList(
    val id: Int = 0, // ID autogenerado
    val batchNumber: String,
    val movementType: String,
    val numberOfHeaters: Int,
    val numberOfHeatersFinished: Int,
    val finish: Boolean,
    val sendByEmail: Boolean,
    val createdBy: String?,
    val timeStamp: String, // Usa un formato ISO 8601 (ejemplo: "2025-01-28T14:30:00")
    val notes: String?
)