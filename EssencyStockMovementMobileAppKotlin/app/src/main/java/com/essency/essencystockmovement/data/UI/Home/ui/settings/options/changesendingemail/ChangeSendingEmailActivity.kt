package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesendingemail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseActivity

class ChangeSendingEmailActivity : BaseActivity() {//AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_sending_email)

        // Cargar el fragmento en el contenedor si no está ya cargado
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_settings_email, ChangeSendingEmailFragment())
                .commit()
        }
    }
}
