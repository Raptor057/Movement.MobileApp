package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesenderemail

import android.os.Bundle
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseActivity

class ChangeSenderEmailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_sender_email)

        // Cargar el fragmento si no está ya cargado
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_settings_email_sender, ChangeSenderEmailFragment())
                .commit()
        }
    }
}