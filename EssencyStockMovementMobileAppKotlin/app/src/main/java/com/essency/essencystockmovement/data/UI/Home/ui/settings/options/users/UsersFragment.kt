package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AppUser
import com.essency.essencystockmovement.data.repository.AppUserRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsUsersAddBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//class UsersFragment : Fragment() {
class UsersFragment : BaseFragment() {

    private var _binding: FragmentSettingsUsersAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: AppUserRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsUsersAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa la base de datos y el repositorio
        val dbHelper = MyDatabaseHelper(requireContext())
        userRepository = AppUserRepository(dbHelper)

        setupFormListeners()

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupFormListeners() {
        binding.buttonSaveUser.setOnClickListener {
            val userName = binding.editTextUserName.text.toString()
            val name = binding.editTextName.text.toString()
            val lastName = binding.editTextLastName.text.toString()
            val password = binding.editTextPassword.text.toString()
            val isAdmin = binding.switchIsAdmin.isChecked
            val isEnabled = binding.switchEnable.isChecked

            if (userName.isBlank() || name.isBlank() || lastName.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val newUser = AppUser(
                userName = userName,
                name = name,
                lastName = lastName,
                passwordHash = password, // El hash se generará en el repositorio
                salt = "", // Esto se generará en el repositorio
                createUserDate = currentDateTime,
                isAdmin = isAdmin,
                enable = isEnabled
            )

            val result = userRepository.insert(newUser)
            if (result != -1L) {
                Toast.makeText(requireContext(), "Usuario agregado correctamente.", Toast.LENGTH_SHORT).show()
                clearForm()
            } else {
                Toast.makeText(requireContext(), "Error al agregar el usuario.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearForm() {
        binding.editTextUserName.text.clear()
        binding.editTextName.text.clear()
        binding.editTextLastName.text.clear()
        binding.editTextPassword.text.clear()
        binding.switchIsAdmin.isChecked = false
        binding.switchEnable.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
