package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.TraceabilityStockList

interface ITraceabilityStockListRepository {
    fun insert(traceabilityStock: TraceabilityStockList): Long
    fun getAll(): List<TraceabilityStockList>
    fun getById(id: Int): TraceabilityStockList?
    fun getLastInserted(): TraceabilityStockList? // 🔹 Nuevo método para obtener el último registro
    fun getLastInsertedFinished(): TraceabilityStockList? // 🔹 Nuevo método para obtener el último registro
    fun update(traceabilityStock: TraceabilityStockList): Int
    fun deleteById(id: Int): Int
}