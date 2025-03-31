package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.AuditStockList


interface IAuditStockListRepository {
    fun insert(stock: AuditStockList): Long
    fun getAll(): List<AuditStockList>
    fun getById(id: Int): AuditStockList?
    fun getLastStockListByMovementTypeAndCreatedBy(movementType: String,  createdBy : String): List<AuditStockList>?
    fun update(stock: AuditStockList): Int
    fun deleteById(id: Int): Int
}
