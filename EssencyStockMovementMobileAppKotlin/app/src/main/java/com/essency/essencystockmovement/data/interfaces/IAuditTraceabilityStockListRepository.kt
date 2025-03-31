package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.AuditTraceabilityStockList


interface IAuditTraceabilityStockListRepository {
    fun insert(traceabilityStock: AuditTraceabilityStockList, movementType: String, createdBy: String): Long
    fun getAll(): List<AuditTraceabilityStockList>
    fun getById(id: Int): AuditTraceabilityStockList?
    //fun getLastInserted(): TraceabilityStockList? // 🔹 Nuevo método para obtener el último registro
    fun getLastInsertedFinished(movementType: String,  createdBy : String): AuditTraceabilityStockList? // 🔹 Nuevo método para obtener el último registro
    fun getLastInserted(movementType: String,  createdBy : String): AuditTraceabilityStockList? // 🔹 Nuevo método para obtener el último registro
    fun update(traceabilityStock: AuditTraceabilityStockList): Int
    fun deleteById(id: Int): Int
}
