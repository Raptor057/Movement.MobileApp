package com.essency.essencystockmovement.data.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.Home.HomeActivity
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.repository.AppUserRepository

class LoginActivity : AppCompatActivity() {

    private lateinit var etUserName: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    
    // Repositorio de usuarios
    private lateinit var userRepository: AppUserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Ajusta insets para el layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Instanciar el DBHelper
        val dbHelper = MyDatabaseHelper(this)

        // 2. Crear el repositorio con el DBHelper
        userRepository = AppUserRepository(dbHelper)

        // 3. Referenciar los views
        etUserName = findViewById(R.id.etUserName)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // 4. Escuchar clic en el botón
        btnLogin.setOnClickListener {
            val userName = etUserName.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (userName.isEmpty() || password.isEmpty()) {
                // Mostrar mensaje de campos vacíos
                Toast.makeText(
                    this,
                    getString(R.string.error_empty_fields), // Reemplazado con recurso de cadena
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                // Buscar al usuario en la DB (ejemplo simple)
                val LoginUser = userRepository.login(userName, password)

                if (LoginUser) {
                    // Login exitoso
                    // Login exitoso
                    Toast.makeText(
                        this,
                        getString(R.string.login_success, userName), // Usando cadena con formato
                        Toast.LENGTH_SHORT
                    ).show()
                    // Opcional: navegar a MainActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // cerrar la pantalla de login
                } else {
                    // Usuario o password incorrecto
                    Toast.makeText(
                        this,
                        getString(R.string.login_failed), // Reemplazado con recurso de cadena
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
