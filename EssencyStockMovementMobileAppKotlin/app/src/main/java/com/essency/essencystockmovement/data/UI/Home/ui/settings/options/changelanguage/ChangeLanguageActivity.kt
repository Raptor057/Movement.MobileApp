package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changelanguage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.essency.essencystockmovement.R

class ChangeLanguageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_language)

        // Cargar el fragmento si no se ha cargado previamente
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_settings_language, ChangeLanguageFragment())
                .commit()
        }
    }
}
