package com.essency.essencystockmovement.data.UI

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.essency.essencystockmovement.data.UtilClass.LanguageManager
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val dbHelper = MyDatabaseHelper(newBase)
        val languageManager = LanguageManager(newBase, dbHelper.readableDatabase)

        // Obtener el idioma activo
        val languageCode = languageManager.getActiveLanguage()

        // Aplicar configuración de idioma
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        super.attachBaseContext(newBase.createConfigurationContext(config))
    }
}