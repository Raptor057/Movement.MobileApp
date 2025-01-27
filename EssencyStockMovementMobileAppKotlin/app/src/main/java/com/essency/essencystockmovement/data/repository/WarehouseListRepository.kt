package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.essency.essencystockmovement.data.interfaces.IWarehouseListRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.WarehouseList

class WarehouseListRepository(private val dbHelper: MyDatabaseHelper) : IWarehouseListRepository {

    override fun insertWarehouse(warehouse: WarehouseList): Long {
        val database = dbHelper.writableDatabase

        // Validar que no exista un warehouse con el mismo nombre
        if (isWarehouseExists(warehouse.warehouse, database)) {
            throw IllegalArgumentException("The warehouse '${warehouse.warehouse}' already exists.")
        }

        val contentValues = ContentValues().apply {
            put("Warehouse", warehouse.warehouse)
        }
        return database.insert("WarehouseList", null, contentValues)
    }

    override fun updateWarehouse(warehouse: WarehouseList): Int {
        val database = dbHelper.writableDatabase

        // Validar que el warehouse existe antes de actualizar
        if (!isWarehouseExistsById(warehouse.id, database)) {
            throw IllegalArgumentException("The warehouse with ID '${warehouse.id}' does not exist.")
        }

        val contentValues = ContentValues().apply {
            put("Warehouse", warehouse.warehouse)
        }
        return database.update(
            "WarehouseList",
            contentValues,
            "ID = ?",
            arrayOf(warehouse.id.toString())
        )
    }

    override fun getAllWarehouses(): List<WarehouseList> {
        val database = dbHelper.readableDatabase
        val warehouses = mutableListOf<WarehouseList>()
        val cursor = database.rawQuery("SELECT * FROM WarehouseList", null)

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("ID"))
                val warehouse = it.getString(it.getColumnIndexOrThrow("Warehouse"))
                warehouses.add(WarehouseList(id, warehouse))
            }
        }
        return warehouses
    }

    // Validar si un warehouse con el mismo nombre ya existe
    private fun isWarehouseExists(warehouseName: String, database: SQLiteDatabase): Boolean {
        val cursor = database.rawQuery(
            "SELECT COUNT(*) FROM WarehouseList WHERE Warehouse = ?",
            arrayOf(warehouseName)
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(0) > 0
            }
        }
        return false
    }

    // Validar si un warehouse existe por su ID
    private fun isWarehouseExistsById(warehouseId: Int, database: SQLiteDatabase): Boolean {
        val cursor = database.rawQuery(
            "SELECT COUNT(*) FROM WarehouseList WHERE ID = ?",
            arrayOf(warehouseId.toString())
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(0) > 0
            }
        }
        return false
    }
}
