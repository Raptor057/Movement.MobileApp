package com.essency.essencystockmovement.data.UI

import android.content.Context
import android.content.res.Configuration
import androidx.fragment.app.Fragment
import com.essency.essencystockmovement.data.UtilClass.LanguageManager
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import java.util.Locale

open class BaseFragment : Fragment() {

    override fun onAttach(context: Context) {
        val dbHelper = MyDatabaseHelper(context)
        val languageManager = LanguageManager(context, dbHelper.readableDatabase)

        // Obtener el idioma activo
        val languageCode = languageManager.getActiveLanguage()

        // Aplicar configuración de idioma
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        val localizedContext = context.createConfigurationContext(config)
        super.onAttach(localizedContext)
    }
}