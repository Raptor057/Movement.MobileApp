package com.essency.essencystockmovement.data.model

data class TraceabilityStockList(
    val id: Int = 0,
    val batchNumber: String,
    val movementType: String,
    val numberOfHeaters: Int,
    val numberOfHeatersFinished: Int,
    val finish: Boolean,  // Debe estar presente
    val sendByEmail: Boolean,
    val createdBy: String?,
    val source: String,
    val destination: String,
    val timeStamp: String,
    val notes: String?
)
