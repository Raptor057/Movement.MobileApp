package com.essency.essencystockmovement.data.model

data class AuditStockList(
    val id: Int = 0,
    val idTraceabilityStockList: Int,
    val company: String,
    val source: String,
    val sourceLoc: String?,
    val destination: String,
    val destinationLoc: String?,
    val pallet: String?,                // Nuevo campo
    val partNo: String,
    val rev: String,
    val lot: String,
    val qty: Int,
    val productionDate: String?,       // Nuevo campo
    val countryOfProduction: String?,  // Nuevo campo
    val serialNumber: String?,         // Nuevo campo
    val date: String,
    val timeStamp: String,
    val user: String,
    val contBolNum: String
)
