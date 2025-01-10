package com.essency.essencystockmovement.data.model

import java.time.LocalDateTime

data class StockList(
    val id: Int = 0, // SQLite autoincrement
    val idStock: Int,
    val company: String,
    val source: String,
    val soucreLoc: String?,
    val destination: String,
    val destinationLoc: String?,
    val partNo: String,
    val rev: String,
    val lot: String,
    val qty: Int,
    val date: String,
    val timeStamp: LocalDateTime,
    val user: String,
    val contBolNum: String
)