package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.essency.essencystockmovement.R

class UsersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users) // Asegúrate de usar el layout correcto

        // Verifica si ya existe el fragmento, para evitar cargarlo dos veces
        if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_settings_users_add, AddUserFragment())
                .commit()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_settings_users_get, GetUsersFragment())
                .commit()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_settings_users_update, UpdateUserFragment())
                .commit()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_settings_users_delete, DeleteUserFragment())
                .commit()
        }
    }
}
