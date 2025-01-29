package com.essency.essencystockmovement.data.repository
import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.IStockListRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.StockList

class StockListRepository(private val dbHelper: MyDatabaseHelper) : IStockListRepository {

    override fun insert(stock: StockList): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("Company", stock.company)
            put("Source", stock.source)
            put("SoucreLoc", stock.sourceLoc)
            put("Destination", stock.destination)
            put("DestinationLoc", stock.destinationLoc)
            put("PartNo", stock.partNo)
            put("Rev", stock.rev)
            put("Lot", stock.lot)
            put("Qty", stock.qty)
            put("Date", stock.date)
            put("TimeStamp", stock.timeStamp)
            put("User", stock.user)
            put("ContBolNum", stock.contBolNum)
        }
        val id = db.insert("StockList", null, values)
        db.close()
        return id
    }

    override fun getAll(): List<StockList> {
        val db = dbHelper.readableDatabase
        val stockList = mutableListOf<StockList>()
        val cursor = db.rawQuery("SELECT * FROM StockList", null)

        cursor.use {
            while (it.moveToNext()) {
                stockList.add(cursorToStock(it))
            }
        }

        db.close()
        return stockList
    }

    override fun getById(id: Int): StockList? {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM StockList WHERE ID = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        var stock: StockList? = null

        cursor.use {
            if (it.moveToFirst()) {
                stock = cursorToStock(it)
            }
        }

        db.close()
        return stock
    }

    override fun update(stock: StockList): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("Company", stock.company)
            put("Source", stock.source)
            put("SoucreLoc", stock.sourceLoc)
            put("Destination", stock.destination)
            put("DestinationLoc", stock.destinationLoc)
            put("PartNo", stock.partNo)
            put("Rev", stock.rev)
            put("Lot", stock.lot)
            put("Qty", stock.qty)
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
            company = cursor.getString(cursor.getColumnIndexOrThrow("Company")),
            source = cursor.getString(cursor.getColumnIndexOrThrow("Source")),
            sourceLoc = cursor.getString(cursor.getColumnIndexOrThrow("SoucreLoc")),
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