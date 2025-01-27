package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import com.essency.essencystockmovement.data.interfaces.IEmailSenderRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.EmailSender

class EmailSenderRepository(private val dbHelper: MyDatabaseHelper) : IEmailSenderRepository {

    override fun getEmailSender(): EmailSender? {
        val database = dbHelper.readableDatabase
        val query = "SELECT * FROM EmailSender LIMIT 1"
        val cursor = database.rawQuery(query, null)

        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndexOrThrow("ID"))
                val email = it.getString(it.getColumnIndexOrThrow("Email"))
                val password = it.getString(it.getColumnIndexOrThrow("Password"))
                return EmailSender(id, email, password)
            }
        }
        return null
    }

    override fun updateEmailSender(emailSender: EmailSender): Int {
        val database = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("Email", emailSender.email)
            put("Password", emailSender.password)
        }
        return database.update(
            "EmailSender",
            contentValues,
            "ID = ?",
            arrayOf(emailSender.id.toString())
        )
    }
}