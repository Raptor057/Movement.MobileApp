package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.TraceabilityStockList

interface ITraceabilityStockListRepository {
    fun insert(traceabilityStock: TraceabilityStockList, movementType: String, createdBy: String): Long
    fun getAll(): List<TraceabilityStockList>
    fun getById(id: Int): TraceabilityStockList?
    //fun getLastInserted(): TraceabilityStockList? // 🔹 Nuevo método para obtener el último registro
    fun getLastInsertedFinished(movementType: String,  createdBy : String): TraceabilityStockList? // 🔹 Nuevo método para obtener el último registro
    fun getLastInserted(movementType: String,  createdBy : String): TraceabilityStockList? // 🔹 Nuevo método para obtener el último registro
    fun update(traceabilityStock: TraceabilityStockList): Int
    fun deleteById(id: Int): Int
}