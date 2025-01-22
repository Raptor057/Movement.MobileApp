package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.IStockListRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.StockList

class StockListRepository(private val dbHelper: MyDatabaseHelper) : IStockListRepository {

    override fun getAllStockList(): List<StockList> {
        val db = dbHelper.readableDatabase
        val stockList = mutableListOf<StockList>()

        val query = "SELECT * FROM StockList"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                stockList.add(cursorToStockList(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return stockList
    }

    override fun getStockListById(id: Int): StockList? {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM StockList WHERE ID = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        var stockList: StockList? = null
        if (cursor.moveToFirst()) {
            stockList = cursorToStockList(cursor)
        }

        cursor.close()
        db.close()
        return stockList
    }

    override fun insertStockList(stockList: StockList): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IDStock", stockList.idStock)
            put("Company", stockList.company)
            put("Source", stockList.source)
            put("SoucreLoc", stockList.sourceloc)
            put("Destination", stockList.destination)
            put("DestinationLoc", stockList.destinationLoc)
            put("PartNo", stockList.partNo)
            put("Rev", stockList.rev)
            put("Lot", stockList.lot)
            put("Qty", stockList.qty)
            put("Date", stockList.date)
            put("TimeStamp", stockList.timeStamp)
            put("User", stockList.user)
            put("ContBolNum", stockList.contBolNum)
        }

        val result = db.insert("StockList", null, values)
        db.close()
        return result
    }

    override fun updateStockList(stockList: StockList): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IDStock", stockList.idStock)
            put("Company", stockList.company)
            put("Source", stockList.source)
            put("SoucreLoc", stockList.sourceloc)
            put("Destination", stockList.destination)
            put("DestinationLoc", stockList.destinationLoc)
            put("PartNo", stockList.partNo)
            put("Rev", stockList.rev)
            put("Lot", stockList.lot)
            put("Qty", stockList.qty)
            put("Date", stockList.date)
            put("TimeStamp", stockList.timeStamp)
            put("User", stockList.user)
            put("ContBolNum", stockList.contBolNum)
        }

        val rowsUpdated = db.update(
            "StockList",
            values,
            "ID = ?",
            arrayOf(stockList.id.toString())
        )

        db.close()
        return rowsUpdated > 0
    }

    override fun deleteStockListById(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("StockList", "ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted > 0
    }

    override fun getStockListByDateRange(startDate: String, endDate: String): List<StockList> {
        val db = dbHelper.readableDatabase
        val stockList = mutableListOf<StockList>()

        val query = "SELECT * FROM StockList WHERE Date BETWEEN ? AND ?"
        val cursor = db.rawQuery(query, arrayOf(startDate, endDate))

        if (cursor.moveToFirst()) {
            do {
                stockList.add(cursorToStockList(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return stockList
    }

    private fun cursorToStockList(cursor: Cursor): StockList {
        return StockList(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
            idStock = cursor.getInt(cursor.getColumnIndexOrThrow("IDStock")),
            company = cursor.getString(cursor.getColumnIndexOrThrow("Company")),
            source = cursor.getString(cursor.getColumnIndexOrThrow("Source")),
            sourceloc = cursor.getString(cursor.getColumnIndexOrThrow("SoucreLoc")),
            destination = cursor.getString(cursor.getColumnIndexOrThrow("Destination")),
            destinationLoc = cursor.getString(cursor.getColumnIndexOrThrow("DestinationLoc")),
            partNo = cursor.getString(cursor.getColumnIndexOrThrow("PartNo")),
            rev = cursor.getString(cursor.getColumnIndexOrThrow("Rev")),
            lot = cursor.getString(cursor.getColumnIndexOrThrow("Lot")),
            qty = cursor.getInt(cursor.getColumnIndexOrThrow("Qty")),
            date = cursor.getString(cursor.getColumnIndexOrThrow("Date")),
            timeStamp = cursor.getString(cursor.getColumnIndexOrThrow("TimeStamp")),
            user = cursor.getString(cursor.getColumnIndexOrThrow("User")),
            contBolNum = cursor.getString(cursor.getColumnIndexOrThrow("ContBolNum"))
        )
    }
}
