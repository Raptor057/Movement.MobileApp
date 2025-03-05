package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.IStockListRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.StockList

class StockListRepository(private val dbHelper: MyDatabaseHelper) : IStockListRepository {

    override fun insert(stock: StockList): Long {
        val db = dbHelper.writableDatabase

        try {
            // 🔍 Verificar si ya existe un registro con el mismo `PartNo` y `Lot`
            val query = "SELECT ID, Qty FROM StockList WHERE PartNo = ? AND Lot = ? ORDER BY ID DESC"
            val cursor = db.rawQuery(query, arrayOf(stock.partNo, stock.lot))

            if (cursor.moveToFirst()) {
                // 🔄 Si ya existe, se actualiza la cantidad (`Qty`)
                val existingId = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                val existingQty = cursor.getInt(cursor.getColumnIndexOrThrow("Qty"))

                val newQty = existingQty + stock.qty // 🔹 Sumar la cantidad nueva a la existente

                val values = ContentValues().apply {
                    put("Qty", newQty)
                    put("TimeStamp", stock.timeStamp) // 🔄 Actualizar `TimeStamp`
                }

                val rowsUpdated = db.update("StockList", values, "ID = ?", arrayOf(existingId.toString()))
                cursor.close()
                return rowsUpdated.toLong() // ✅ Retorna el número de filas actualizadas
            }

            cursor.close()

            // 🆕 Si no existe, se inserta un nuevo registro con los nuevos campos
            val values = ContentValues().apply {
                put("IDTraceabilityStockList", stock.idTraceabilityStockList) // 🔹 Nuevo campo agregado
                put("Company", stock.company)
                put("Source", stock.source)
                put("SourceLoc", stock.sourceLoc)
                put("Destination", stock.destination)
                put("DestinationLoc", stock.destinationLoc)
                put("Pallet", stock.pallet) // 🔹 Nuevo campo agregado
                put("PartNo", stock.partNo)
                put("Rev", stock.rev)
                put("Lot", stock.lot)
                put("Qty", stock.qty)
                put("ProductionDate", stock.productionDate) // 🔹 Nuevo campo agregado
                put("CountryOfProduction", stock.countryOfProduction) // 🔹 Nuevo campo agregado
                put("SerialNumber", stock.serialNumber) // 🔹 Nuevo campo agregado
                put("Date", stock.date)
                put("TimeStamp", stock.timeStamp)
                put("User", stock.user)
                put("ContBolNum", stock.contBolNum)
            }

            return db.insert("StockList", null, values)

        } catch (e: Exception) {
            e.printStackTrace()
            return -1L
        } finally {
            db.close()
        }
    }

    override fun getAll(): List<StockList> {
        val db = dbHelper.readableDatabase
        val stockList = mutableListOf<StockList>()
        var cursor = db.rawQuery("SELECT SL.* FROM StockList SL INNER JOIN TraceabilityStockList TSL ON SL.IDTraceabilityStockList = TSL.ID ORDER BY SL.ID DESC", null)
        //val cursor = db.rawQuery("SELECT * FROM StockList ORDER BY ID DESC", null)

        cursor.use {
            while (it.moveToNext()) {
                stockList.add(cursorToStock(it))
            }
        }

        return stockList // 🔹 NO cerramos la base de datos aquí
    }

    override fun getById(id: Int): StockList? {
        val db = dbHelper.readableDatabase
        var stock: StockList? = null
        val cursor = db.rawQuery("SELECT * FROM StockList WHERE ID = ? ORDER BY ID DESC", arrayOf(id.toString()))

        cursor.use {
            if (it.moveToFirst()) {
                stock = cursorToStock(it)
            }
        }

        return stock // 🔹 NO cerramos la base de datos aquí
    }

    override fun update(stock: StockList): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IDTraceabilityStockList", stock.idTraceabilityStockList) // 🔹 Nuevo campo agregado
            put("Company", stock.company)
            put("Source", stock.source)
            put("SourceLoc", stock.sourceLoc)
            put("Destination", stock.destination)
            put("DestinationLoc", stock.destinationLoc)
            put("Pallet", stock.pallet) // 🔹 Nuevo campo agregado
            put("PartNo", stock.partNo)
            put("Rev", stock.rev)
            put("Lot", stock.lot)
            put("Qty", stock.qty)
            put("ProductionDate", stock.productionDate) // 🔹 Nuevo campo agregado
            put("CountryOfProduction", stock.countryOfProduction) // 🔹 Nuevo campo agregado
            put("SerialNumber", stock.serialNumber) // 🔹 Nuevo campo agregado
            put("Date", stock.date)
            put("TimeStamp", stock.timeStamp)
            put("User", stock.user)
            put("ContBolNum", stock.contBolNum)
        }

        val rowsAffected = db.update(
            "StockList",
            values,
            "ID = ?",
            arrayOf(stock.id.toString())
        )
        db.close()
        return rowsAffected
    }

    override fun deleteById(id: Int): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("StockList", "ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    private fun cursorToStock(cursor: Cursor): StockList {
        return StockList(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
            idTraceabilityStockList = cursor.getInt(cursor.getColumnIndexOrThrow("IDTraceabilityStockList")), // 🔹 Nuevo campo agregado
            company = cursor.getString(cursor.getColumnIndexOrThrow("Company")),
            source = cursor.getString(cursor.getColumnIndexOrThrow("Source")),
            sourceLoc = cursor.getColumnIndex("SourceLoc").takeIf { it != -1 }?.let { cursor.getString(it) },
            destination = cursor.getString(cursor.getColumnIndexOrThrow("Destination")),
            destinationLoc = cursor.getColumnIndex("DestinationLoc").takeIf { it != -1 }?.let { cursor.getString(it) },
            pallet = cursor.getColumnIndex("Pallet").takeIf { it != -1 }?.let { cursor.getString(it) }, // 🔹 Nuevo campo agregado
            partNo = cursor.getString(cursor.getColumnIndexOrThrow("PartNo")),
            rev = cursor.getString(cursor.getColumnIndexOrThrow("Rev")),
            lot = cursor.getString(cursor.getColumnIndexOrThrow("Lot")),
            qty = cursor.getInt(cursor.getColumnIndexOrThrow("Qty")),
            productionDate = cursor.getColumnIndex("ProductionDate").takeIf { it != -1 }?.let { cursor.getString(it) }, // 🔹 Nuevo campo agregado
            countryOfProduction = cursor.getColumnIndex("CountryOfProduction").takeIf { it != -1 }?.let { cursor.getString(it) }, // 🔹 Nuevo campo agregado
            serialNumber = cursor.getColumnIndex("SerialNumber").takeIf { it != -1 }?.let { cursor.getString(it) }, // 🔹 Nuevo campo agregado
            date = cursor.getString(cursor.getColumnIndexOrThrow("Date")),
            timeStamp = cursor.getString(cursor.getColumnIndexOrThrow("TimeStamp")),
            user = cursor.getString(cursor.getColumnIndexOrThrow("User")),
            contBolNum = cursor.getString(cursor.getColumnIndexOrThrow("ContBolNum"))
        )
    }
}
