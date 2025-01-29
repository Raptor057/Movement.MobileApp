package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.ITraceabilityStockListRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.TraceabilityStockList

class TraceabilityStockListRepository(private val dbHelper: MyDatabaseHelper) : ITraceabilityStockListRepository {

    override fun insert(traceabilityStock: TraceabilityStockList): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("BatchNumber", traceabilityStock.batchNumber)
            put("MovementType", traceabilityStock.movementType)
            put("NumberOfHeaters", traceabilityStock.numberOfHeaters)
            put("NumberOfHeatersFinished", traceabilityStock.numberOfHeatersFinished)
            put("Finish", if (traceabilityStock.finish) 1 else 0)
            put("SendByEmail", if (traceabilityStock.sendByEmail) 1 else 0)
            put("CreatedBy", traceabilityStock.createdBy)
            put("TimeStamp", traceabilityStock.timeStamp)
            put("Notes", traceabilityStock.notes)
        }
        val id = db.insert("TraceabilityStockList", null, values)
        db.close()
        return id
    }

    override fun getAll(): List<TraceabilityStockList> {
        val db = dbHelper.readableDatabase
        val stockList = mutableListOf<TraceabilityStockList>()
        val cursor = db.rawQuery("SELECT * FROM TraceabilityStockList", null)

        cursor.use {
            while (it.moveToNext()) {
                stockList.add(cursorToTraceabilityStock(it))
            }
        }

        db.close()
        return stockList
    }

    override fun getById(id: Int): TraceabilityStockList? {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM TraceabilityStockList WHERE ID = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        var stock: TraceabilityStockList? = null

        cursor.use {
            if (it.moveToFirst()) {
                stock = cursorToTraceabilityStock(it)
            }
        }

        db.close()
        return stock
    }

    override fun update(traceabilityStock: TraceabilityStockList): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("BatchNumber", traceabilityStock.batchNumber)
            put("MovementType", traceabilityStock.movementType)
            put("NumberOfHeaters", traceabilityStock.numberOfHeaters)
            put("NumberOfHeatersFinished", traceabilityStock.numberOfHeatersFinished)
            put("Finish", if (traceabilityStock.finish) 1 else 0)
            put("SendByEmail", if (traceabilityStock.sendByEmail) 1 else 0)
            put("CreatedBy", traceabilityStock.createdBy)
            put("TimeStamp", traceabilityStock.timeStamp)
            put("Notes", traceabilityStock.notes)
        }

        val rowsAffected = db.update(
            "TraceabilityStockList",
            values,
            "ID = ?",
            arrayOf(traceabilityStock.id.toString())
        )
        db.close()
        return rowsAffected
    }

    override fun deleteById(id: Int): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("TraceabilityStockList", "ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    private fun cursorToTraceabilityStock(cursor: Cursor): TraceabilityStockList {
        return TraceabilityStockList(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
            batchNumber = cursor.getString(cursor.getColumnIndexOrThrow("BatchNumber")),
            movementType = cursor.getString(cursor.getColumnIndexOrThrow("MovementType")),
            numberOfHeaters = cursor.getInt(cursor.getColumnIndexOrThrow("NumberOfHeaters")),
            numberOfHeatersFinished = cursor.getInt(cursor.getColumnIndexOrThrow("NumberOfHeatersFinished")),
            finish = cursor.getInt(cursor.getColumnIndexOrThrow("Finish")) == 1,
            sendByEmail = cursor.getInt(cursor.getColumnIndexOrThrow("SendByEmail")) == 1,
            createdBy = cursor.getString(cursor.getColumnIndexOrThrow("CreatedBy")),
            timeStamp = cursor.getString(cursor.getColumnIndexOrThrow("TimeStamp")),
            notes = cursor.getString(cursor.getColumnIndexOrThrow("Notes"))
        )
    }
}