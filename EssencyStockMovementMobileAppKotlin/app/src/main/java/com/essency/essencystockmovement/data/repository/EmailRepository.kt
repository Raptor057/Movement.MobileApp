// EmailRepository.kt
package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.IEmailRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AppConfigurationEmail

class EmailRepository(private val dbHelper: MyDatabaseHelper) : IEmailRepository {

    override fun getEmail(): AppConfigurationEmail? {
        val db = dbHelper.readableDatabase
        val query = "SELECT Email FROM AppConfigurationEmail LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, null)

        var emailConfig: AppConfigurationEmail? = null
        if (cursor.moveToFirst()) {
            val email = cursor.getString(cursor.getColumnIndexOrThrow("Email"))
            emailConfig = AppConfigurationEmail(email)
        }

        cursor.close()
        db.close()
        return emailConfig
    }

    override fun updateEmail(newEmail: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("Email", newEmail)
        }

        val rowsUpdated = db.update("AppConfigurationEmail", values, null, null)
        db.close()
        return rowsUpdated > 0
    }
}
