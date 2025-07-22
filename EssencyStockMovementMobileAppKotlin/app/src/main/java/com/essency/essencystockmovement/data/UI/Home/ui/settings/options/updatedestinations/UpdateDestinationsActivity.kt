package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.updatedestinations
import android.os.Bundle
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseActivity


class UpdateDestinationsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_destination)

        // Cargar el fragmento en el contenedor si no está ya cargado
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UpdateDestinationsFragment())
                .commit()
        }
    }
}