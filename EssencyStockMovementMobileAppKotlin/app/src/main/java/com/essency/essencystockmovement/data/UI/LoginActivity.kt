package com.essency.essencystockmovement.data.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.Home.HomeActivity
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AppUser
import com.essency.essencystockmovement.data.repository.AppUserRepository

class LoginActivity : BaseActivity() {

    private lateinit var etUserName: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var userRepository: AppUserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = MyDatabaseHelper(this)
        userRepository = AppUserRepository(dbHelper)

        etUserName = findViewById(R.id.etUserName)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val userName = etUserName.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loggedInUser = userRepository.getByUserName(userName)

            if (loggedInUser != null && userRepository.login(userName, password)) {
                // Guardar los datos del usuario en SharedPreferences
                saveUserToPreferences(loggedInUser)

                Toast.makeText(this, getString(R.string.login_success, userName), Toast.LENGTH_SHORT).show()

                // Navegar a HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToPreferences(user: AppUser) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("userID", user.id)
            putString("userName", user.userName)
            putString("userType", user.userType)
            putString("name", user.name)
            putString("lastName", user.lastName)
            putBoolean("isAdmin", user.isAdmin)
            putBoolean("enable", user.enable)
            apply()
        }
    }
}
