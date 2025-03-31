package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.IAuditStockListRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AuditStockList

class AuditStockListRepository(private val dbHelper: MyDatabaseHelper) : IAuditStockListRepository {

    override fun insert(stock: AuditStockList): Long {
        val db = dbHelper.writableDatabase

        try {
            val values = ContentValues().apply {
                put("IDTraceabilityStockList", stock.idTraceabilityStockList)
                put("Company", stock.company)
                put("Source", stock.source)
                put("SourceLoc", stock.sourceLoc)
                put("Destination", stock.destination)
                put("DestinationLoc", stock.destinationLoc)
                put("Pallet", stock.pallet)
                put("PartNo", stock.partNo)
                put("Rev", stock.rev)
                put("Lot", stock.lot)
                put("Qty", stock.qty)
                put("ProductionDate", stock.productionDate)
                put("CountryOfProduction", stock.countryOfProduction)
                put("SerialNumber", stock.serialNumber)
                put("Date", stock.date)
                put("TimeStamp", stock.timeStamp)
                put("User", stock.user)
                put("ContBolNum", stock.contBolNum)
            }

            return db.insert("AuditStockList", null, values)

        } catch (e: Exception) {
            e.printStackTrace()
            return -1L
        } finally {
            db.close()
        }
    }

    override fun getAll(): List<AuditStockList> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<AuditStockList>()
        val cursor = db.rawQuery("SELECT * FROM AuditStockList ORDER BY ID DESC", null)

        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToStock(it))
            }
        }

        return list
    }

    override fun getById(id: Int): AuditStockList? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM AuditStockList WHERE ID = ?", arrayOf(id.toString()))
        var stock: AuditStockList? = null

        cursor.use {
            if (it.moveToFirst()) {
                stock = cursorToStock(it)
            }
        }

        return stock
    }

    override fun getLastStockListByMovementTypeAndCreatedBy(movementType: String, createdBy: String): List<AuditStockList>? {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<AuditStockList>()
        val cursor = db.rawQuery(
            "SELECT SL.* FROM AuditStockList SL " +
                    "INNER JOIN AuditTraceabilityStockList TSL ON SL.IDTraceabilityStockList = TSL.ID " +
                    "WHERE TSL.MovementType = ? AND TSL.CreatedBy = ? AND TSL.Finish = 0 " +
                    "ORDER BY SL.ID DESC",
            arrayOf(movementType, createdBy)
        )

        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToStock(it))
            }
        }

        return list
    }

    override fun update(stock: AuditStockList): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IDTraceabilityStockList", stock.idTraceabilityStockList)
            put("Company", stock.company)
            put("Source", stock.source)
            put("SourceLoc", stock.sourceLoc)
            put("Destination", stock.destination)
            put("DestinationLoc", stock.destinationLoc)
            put("Pallet", stock.pallet)
            put("PartNo", stock.partNo)
            put("Rev", stock.rev)
            put("Lot", stock.lot)
            put("Qty", stock.qty)
            put("ProductionDate", stock.productionDate)
            put("CountryOfProduction", stock.countryOfProduction)
            put("SerialNumber", stock.serialNumber)
            put("Date", stock.date)
            put("TimeStamp", stock.timeStamp)
            put("User", stock.user)
            put("ContBolNum", stock.contBolNum)
        }

        val rowsAffected = db.update("AuditStockList", values, "ID = ?", arrayOf(stock.id.toString()))
        db.close()
        return rowsAffected
    }

    override fun deleteById(id: Int): Int {
        val db = dbHelper.writableDatabase
        val rows = db.delete("AuditStockList", "ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    private fun cursorToStock(cursor: Cursor): AuditStockList {
        return AuditStockList(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
            idTraceabilityStockList = cursor.getInt(cursor.getColumnIndexOrThrow("IDTraceabilityStockList")),
            company = cursor.getString(cursor.getColumnIndexOrThrow("Company")),
            source = cursor.getString(cursor.getColumnIndexOrThrow("Source")),
            sourceLoc = cursor.getColumnIndex("SourceLoc").takeIf { it != -1 }?.let { cursor.getString(it) },
            destination = cursor.getString(cursor.getColumnIndexOrThrow("Destination")),
            destinationLoc = cursor.getColumnIndex("DestinationLoc").takeIf { it != -1 }?.let { cursor.getString(it) },
            pallet = cursor.getColumnIndex("Pallet").takeIf { it != -1 }?.let { cursor.getString(it) },
            partNo = cursor.getString(cursor.getColumnIndexOrThrow("PartNo")),
            rev = cursor.getString(cursor.getColumnIndexOrThrow("Rev")),
            lot = cursor.getString(cursor.getColumnIndexOrThrow("Lot")),
            qty = cursor.getInt(cursor.getColumnIndexOrThrow("Qty")),
            productionDate = cursor.getColumnIndex("ProductionDate").takeIf { it != -1 }?.let { cursor.getString(it) },
            countryOfProduction = cursor.getColumnIndex("CountryOfProduction").takeIf { it != -1 }?.let { cursor.getString(it) },
            serialNumber = cursor.getColumnIndex("SerialNumber").takeIf { it != -1 }?.let { cursor.getString(it) },
            date = cursor.getString(cursor.getColumnIndexOrThrow("Date")),
            timeStamp = cursor.getString(cursor.getColumnIndexOrThrow("TimeStamp")),
            user = cursor.getString(cursor.getColumnIndexOrThrow("User")),
            contBolNum = cursor.getString(cursor.getColumnIndexOrThrow("ContBolNum"))
        )
    }
}