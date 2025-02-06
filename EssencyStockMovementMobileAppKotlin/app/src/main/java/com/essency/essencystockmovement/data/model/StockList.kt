package com.essency.essencystockmovement.data.model

data class StockList(
    val id: Int = 0, // ID autoincremental
    val idTraceabilityStockList: Int, // 🔹 Nuevo campo agregado
    val company: String,
    val source: String,
    val sourceLoc: String?,  // Puede ser `null`
    val destination: String,
    val destinationLoc: String?,  // Puede ser `null`
    val pallet: String?,  // Puede ser `null`
    val partNo: String,
    val rev: String,
    val lot: String,
    val qty: Int,
    val productionDate: String?,  // Puede ser `null`
    val countryOfProduction: String?,  // Puede ser `null`
    val serialNumber: String?,  // Puede ser `null`
    val date: String, // Formato: YYYY-MM-DD
    val timeStamp: String, // Formato ISO 8601 "2025-01-28T14:30:00"
    val user: String,
    val contBolNum: String
)