package com.essency.essencystockmovement.data.model

data class AuditTraceabilityStockList(
    val id: Int = 0,
    val batchNumber: String,
    val movementType: String,
    val numberOfHeaters: Int,
    val numberOfHeatersFinished: Int,
    val finish: Boolean,
    val sendByEmail: Boolean,
    val createdBy: String?,
    val source: String,
    val destination: String,
    val timeStamp: String,
    val notes: String?
)
