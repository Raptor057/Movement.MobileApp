package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.IAuditTraceabilityStockListRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AuditTraceabilityStockList

open class AuditTraceabilityStockListRepository(private val dbHelper: MyDatabaseHelper) : IAuditTraceabilityStockListRepository
{
    override fun insert(traceabilityStock: AuditTraceabilityStockList, movementType: String, createdBy: String): Long {
        val db = dbHelper.writableDatabase

        try {
            val last = getLastInserted(movementType, createdBy)

            if (last != null && last.finish == 0 && last.numberOfHeatersFinished != last.numberOfHeaters) {
                return -1L
            }

            val values = ContentValues().apply {
                put("BatchNumber", traceabilityStock.batchNumber)
                put("MovementType", traceabilityStock.movementType)
                put("NumberOfHeaters", traceabilityStock.numberOfHeaters)
                put("NumberOfHeatersFinished", traceabilityStock.numberOfHeatersFinished)
                put("Finish", traceabilityStock.finish)
                put("SendByEmail", traceabilityStock.sendByEmail)
                put("CreatedBy", traceabilityStock.createdBy)
                put("Source", traceabilityStock.source)
                put("Destination", traceabilityStock.destination)
                put("TimeStamp", traceabilityStock.timeStamp)
                put("Notes", traceabilityStock.notes)
            }

            return db.insert("AuditTraceabilityStockList", null, values)
        } catch (e: Exception) {
            e.printStackTrace()
            return -1L
        } finally {
            db.close()
        }
    }

    override fun getAll(): List<AuditTraceabilityStockList> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<AuditTraceabilityStockList>()
        val cursor = db.rawQuery("SELECT * FROM AuditTraceabilityStockList", null)

        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToModel(it))
            }
        }

        return list
    }

    override fun getById(id: Int): AuditTraceabilityStockList? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM AuditTraceabilityStockList WHERE ID = ?", arrayOf(id.toString()))
        var result: AuditTraceabilityStockList? = null

        cursor.use {
            if (it.moveToFirst()) {
                result = cursorToModel(it)
            }
        }

        return result
    }

    override fun getLastInsertedFinished(movementType: String, createdBy: String): AuditTraceabilityStockList? {
        return getAuditRow(movementType, createdBy, finish = 1)
    }

    override fun getLastInserted(movementType: String, createdBy: String): AuditTraceabilityStockList? {
        return getAuditRow(movementType, createdBy, finish = 0)
    }

    override fun update(traceabilityStock: AuditTraceabilityStockList): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("BatchNumber", traceabilityStock.batchNumber)
            put("MovementType", traceabilityStock.movementType)
            put("NumberOfHeaters", traceabilityStock.numberOfHeaters)
            put("NumberOfHeatersFinished", traceabilityStock.numberOfHeatersFinished)
            put("Finish", traceabilityStock.finish)
            put("SendByEmail", traceabilityStock.sendByEmail)
            put("CreatedBy", traceabilityStock.createdBy)
            put("Source", traceabilityStock.source)
            put("Destination", traceabilityStock.destination)
            put("TimeStamp", traceabilityStock.timeStamp)
            put("Notes", traceabilityStock.notes)
        }

        val result = db.update("AuditTraceabilityStockList", values, "ID = ?", arrayOf(traceabilityStock.id.toString()))
        db.close()
        return result
    }

    override fun deleteById(id: Int): Int {
        val db = dbHelper.writableDatabase
        val rows = db.delete("AuditTraceabilityStockList", "ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    private fun getAuditRow(movementType: String, createdBy: String, finish: Int): AuditTraceabilityStockList? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM AuditTraceabilityStockList WHERE Finish = ? AND MovementType = ? AND CreatedBy = ? ORDER BY ID DESC LIMIT 1",
            arrayOf(finish.toString(), movementType, createdBy)
        )

        cursor.use {
            if (it.moveToFirst()) {
                return cursorToModel(it)
            }
        }

        return null
    }

    private fun cursorToModel(cursor: Cursor): AuditTraceabilityStockList {
        return AuditTraceabilityStockList(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
            batchNumber = cursor.getString(cursor.getColumnIndexOrThrow("BatchNumber")),
            movementType = cursor.getString(cursor.getColumnIndexOrThrow("MovementType")),
            numberOfHeaters = cursor.getInt(cursor.getColumnIndexOrThrow("NumberOfHeaters")),
            numberOfHeatersFinished = cursor.getInt(cursor.getColumnIndexOrThrow("NumberOfHeatersFinished")),
            finish = cursor.getInt(cursor.getColumnIndexOrThrow("Finish")),
            sendByEmail = cursor.getInt(cursor.getColumnIndexOrThrow("SendByEmail")),
            createdBy = cursor.getString(cursor.getColumnIndexOrThrow("CreatedBy")),
            source = cursor.getString(cursor.getColumnIndexOrThrow("Source")),
            destination = cursor.getString(cursor.getColumnIndexOrThrow("Destination")),
            timeStamp = cursor.getString(cursor.getColumnIndexOrThrow("TimeStamp")),
            notes = cursor.getString(cursor.getColumnIndexOrThrow("Notes"))
        )
    }
}