package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.StockList

interface IStockListRepository {
    /**
     * Obtiene todos los registros de la tabla StockList.
     */
    fun getAllStockList(): List<StockList>

    /**
     * Obtiene un registro específico de StockList por su ID.
     * @param id ID del registro a buscar.
     */
    fun getStockListById(id: Int): StockList?

    /**
     * Inserta un nuevo registro en la tabla StockList.
     * @param stockList El objeto StockList a insertar.
     */
    fun insertStockList(stockList: StockList): Long

    /**
     * Actualiza un registro existente en la tabla StockList.
     * @param stockList El objeto StockList con los datos actualizados.
     */
    fun updateStockList(stockList: StockList): Boolean

    /**
     * Elimina un registro de la tabla StockList por su ID.
     * @param id ID del registro a eliminar.
     */
    fun deleteStockListById(id: Int): Boolean

    /**
     * Obtiene registros por un rango de fechas.
     * @param startDate Fecha de inicio.
     * @param endDate Fecha de fin.
     */
    fun getStockListByDateRange(startDate: String, endDate: String): List<StockList>
}
