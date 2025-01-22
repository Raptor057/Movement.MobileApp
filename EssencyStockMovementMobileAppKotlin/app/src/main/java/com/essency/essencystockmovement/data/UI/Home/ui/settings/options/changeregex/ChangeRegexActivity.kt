package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changeregex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.essency.essencystockmovement.R

class ChangeRegexActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_regex)

        // Cargar el fragmento en el contenedor si no se ha hecho ya
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_settings_regex, ChangeRegexFragment())
            }
        }
    }
}