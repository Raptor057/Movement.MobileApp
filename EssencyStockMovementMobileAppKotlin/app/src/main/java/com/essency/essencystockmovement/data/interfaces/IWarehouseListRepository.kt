package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.WarehouseList

interface IWarehouseListRepository {
    fun insertWarehouse(warehouse: WarehouseList): Long
    fun updateWarehouse(warehouse: WarehouseList): Int
    fun getAllWarehouses(): List<WarehouseList>
}
