package com.essency.essencystockmovement.data.model

data class StockList(
    val id: Int = 0, // SQLite autoincrement
    val idStock: Int,
    val company: String,
    val source: String,
    val sourceloc: String?,
    val destination: String,
    val destinationLoc: String?,
    val partNo: String,
    val rev: String,
    val lot: String,
    val qty: Int,
    val date: String, // Almacena la fecha como cadena en formato "yyyy-MM-dd"
    val timeStamp: String, // Almacena el timestamp como cadena en formato "yyyy-MM-dd HH:mm:ss"
    val user: String,
    val contBolNum: String
)
