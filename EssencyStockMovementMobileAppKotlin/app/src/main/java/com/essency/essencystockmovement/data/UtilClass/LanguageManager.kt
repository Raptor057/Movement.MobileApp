package com.essency.essencystockmovement.data.UtilClass

import android.content.Context
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.util.Locale

class LanguageManager(private val context: Context, private val database: SQLiteDatabase) {

    // Método para obtener el idioma actual
    fun getActiveLanguage(): String {
        val query = "SELECT LanguageName FROM Language WHERE ActiveLanguage = 1"
        val cursor = database.rawQuery(query, null)
        var language = "en" // Idioma predeterminado
        if (cursor.moveToFirst()) {
            language = cursor.getString(0) // Obtiene el idioma activo
        }
        cursor.close()
        return when (language) {
            "Français" -> "fr"
            "Español" -> "es"
            "English" -> "en"
            else -> "en" // Predeterminado
        }
    }

    // Método para cambiar el idioma
    fun setActiveLanguage(languageName: String) {
        try {
            // Desactiva todos los idiomas
            val disableQuery = "UPDATE Language SET ActiveLanguage = 0"
            database.execSQL(disableQuery)

            // Activa el idioma seleccionado
            val enableQuery = "UPDATE Language SET ActiveLanguage = 1 WHERE LanguageName = ?"
            val statement = database.compileStatement(enableQuery)
            statement.bindString(1, languageName)
            statement.executeUpdateDelete()

            // Cambia el idioma de la aplicación
            applyLanguage(languageName)
        } catch (e: Exception) {
            Log.e("LanguageManager", "Error al cambiar el idioma: ${e.message}")
        }
    }

    // Método para aplicar el idioma en la aplicación
    private fun applyLanguage (languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        context.createConfigurationContext(config)
    }
}