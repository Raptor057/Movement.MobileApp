package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.ILanguageRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.Language


class LanguageRepository(private val dbHelper: MyDatabaseHelper) : ILanguageRepository {

    override fun getAllLanguages(): MutableList<Language> {
        val db = dbHelper.readableDatabase
        val languageList = mutableListOf<Language>()

        val query = "SELECT ID, LanguageName, ActiveLanguage FROM Language"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("LanguageName"))
                val active = cursor.getInt(cursor.getColumnIndexOrThrow("ActiveLanguage")) == 1

                languageList.add(Language(id, name, active))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return languageList
    }

    override fun updateActiveLanguage(id: Int): Boolean {
        val db = dbHelper.writableDatabase

        // Desactivar todos los idiomas
        val deactivateValues = ContentValues().apply {
            put("ActiveLanguage", 0)
        }
        db.update("Language", deactivateValues, null, null)

        // Activar el idioma especificado
        val activateValues = ContentValues().apply {
            put("ActiveLanguage", 1)
        }
        val rowsUpdated = db.update(
            "Language",
            activateValues,
            "ID = ?",
            arrayOf(id.toString())
        )

        db.close()
        return rowsUpdated > 0
    }
}
