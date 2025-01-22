package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.IRegularExpressionRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AppConfigurationRegularExpression

class RegularExpressionRepository(private val dbHelper: MyDatabaseHelper) :
    IRegularExpressionRepository {

    override fun getAllRegularExpressions(): List<AppConfigurationRegularExpression> {
        val db = dbHelper.readableDatabase
        val regexList = mutableListOf<AppConfigurationRegularExpression>()

        val query =
            "SELECT ID, NameRegularExpression, RegularExpression FROM AppConfigurationRegularExpression"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("NameRegularExpression"))
                val regex = cursor.getString(cursor.getColumnIndexOrThrow("RegularExpression"))

                regexList.add(AppConfigurationRegularExpression(id, name, regex))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return regexList
    }

    override fun updateRegularExpression(id: Int, regularExpression: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("RegularExpression", regularExpression) // Actualiza solo la columna RegularExpression
        }

        val rowsUpdated = db.update(
            "AppConfigurationRegularExpression",
            values,
            "ID = ?",
            arrayOf(id.toString())
        )

        db.close()
        return rowsUpdated > 0
    }
}