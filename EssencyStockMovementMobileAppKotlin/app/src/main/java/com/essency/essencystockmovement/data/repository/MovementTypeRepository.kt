package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.IMovementTypeRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.MovementType

class MovementTypeRepository(private val dbHelper: MyDatabaseHelper) : IMovementTypeRepository {

    override fun getAllMovementTypes(): List<MovementType> {
        val db = dbHelper.readableDatabase
        val movementTypeList = mutableListOf<MovementType>()

        val query = "SELECT ID, UserType, Type, Source, Destination FROM MovementType"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                val userType = cursor.getString(cursor.getColumnIndexOrThrow("UserType"))
                val type = cursor.getString(cursor.getColumnIndexOrThrow("Type"))
                val source = cursor.getString(cursor.getColumnIndexOrThrow("Source"))
                val destination = cursor.getString(cursor.getColumnIndexOrThrow("Destination"))

                movementTypeList.add(MovementType(id, userType, type, source, destination))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return movementTypeList
    }

    override fun insertMovementType(movementType: MovementType): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("UserType", movementType.usertype) // Incluimos UserType
            put("Type", movementType.type)
            put("Source", movementType.source)
            put("Destination", movementType.destination)
        }

        val result = db.insert("MovementType", null, values)
        db.close()
        return result != -1L
    }

    override fun updateMovementType(movementType: MovementType): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("UserType", movementType.usertype) // Incluimos UserType
            put("Type", movementType.type)
            put("Source", movementType.source)
            put("Destination", movementType.destination ?: "")
        }

        val rowsUpdated = db.update(
            "MovementType",
            values,
            "ID = ?",
            arrayOf(movementType.id.toString())
        )
        db.close()
        return rowsUpdated > 0
    }

    override fun deleteMovementTypeById(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("MovementType", "ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted > 0
    }

    override fun getDestinationInMovementTypesByTypeandUserType(
        type: String,
        userType: String
    ): String {
        val db = dbHelper.readableDatabase
        val query = "SELECT Destination FROM MovementType WHERE Type = ? AND UserType = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(type, userType))
        var destination = ""
        if (cursor.moveToFirst()) {
            destination = cursor.getString(cursor.getColumnIndexOrThrow("Destination"))
        }
        cursor.close()
        db.close()
        return destination
    }
    override fun getSourceInMovementTypesByTypeandUserType(
        type: String,
        userType: String
    ): String {
        val db = dbHelper.readableDatabase
        val query = "SELECT Source FROM MovementType WHERE Type = ? AND UserType = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(type, userType))
        var source = ""
        if (cursor.moveToFirst()) {
            source = cursor.getString(cursor.getColumnIndexOrThrow("Source"))
        }
        cursor.close()
        db.close()
        return source
    }
}
