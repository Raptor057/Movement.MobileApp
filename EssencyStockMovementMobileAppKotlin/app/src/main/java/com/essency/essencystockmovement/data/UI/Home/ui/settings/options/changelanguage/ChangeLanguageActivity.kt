package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changelanguage

import android.os.Bundle
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseActivity

class ChangeLanguageActivity : BaseActivity() {
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

