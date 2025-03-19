package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.StockList

interface IStockListRepository {
    fun insert(stock: StockList): Long
    fun getAll(): List<StockList>
    fun getById(id: Int): StockList?
    fun getLastStockListByMovementTypeAndCreatedBy(movementType: String,  createdBy : String): List<StockList>?
    fun update(stock: StockList): Int
    fun deleteById(id: Int): Int
}