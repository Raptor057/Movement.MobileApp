package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.essency.essencystockmovement.data.UtilClass.PBKDF2Helper
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AppUser
import com.essency.essencystockmovement.data.repository.AppUserRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsUsersUpdateBinding

class UpdateUserFragment : Fragment() {

    private var _binding: FragmentSettingsUsersUpdateBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: AppUserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsUsersUpdateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa la base de datos y el repositorio
        val dbHelper = MyDatabaseHelper(requireContext())
        userRepository = AppUserRepository(dbHelper)

        setupListeners()

        return root
    }

    private fun setupListeners() {
        // Botón para cargar usuario por ID
        binding.buttonLoadUser.setOnClickListener {
            val userId = binding.editTextUserId.text.toString().toIntOrNull()
            if (userId == null) {
                Toast.makeText(requireContext(), "Ingresa un ID válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = userRepository.getById(userId)
            if (user != null) {
                populateForm(user)
            } else {
                Toast.makeText(requireContext(), "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para actualizar usuario
        binding.buttonUpdateUser.setOnClickListener {
            val userId = binding.editTextUserId.text.toString().toIntOrNull()
            if (userId == null) {
                Toast.makeText(requireContext(), "Ingresa un ID válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val salt = PBKDF2Helper.generateSalt()
            val passwordHash = PBKDF2Helper.hashPassword(binding.editTextNewPassword.text.toString(), salt)

            val updatedUser = AppUser(
                id = userId,
                userName = binding.editTextUpdatedUserName.text.toString(),
                name = binding.editTextUpdatedName.text.toString(),
                lastName = binding.editTextUpdatedLastName.text.toString(),
                passwordHash = Base64.encodeToString(passwordHash, Base64.NO_WRAP),
                salt = Base64.encodeToString(salt, Base64.NO_WRAP),
                createUserDate = "", // La fecha de creación no cambia
                isAdmin = binding.switchUpdatedIsAdmin.isChecked,
                enable = binding.switchUpdatedEnable.isChecked
            )

            val rowsUpdated = userRepository.update(updatedUser)
            if (rowsUpdated > 0) {
                Toast.makeText(requireContext(), "Usuario actualizado correctamente.", Toast.LENGTH_SHORT).show()
                clearForm()
            } else {
                Toast.makeText(requireContext(), "Error al actualizar el usuario.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateForm(user: AppUser) {
        binding.editTextUpdatedUserName.setText(user.userName)
        binding.editTextUpdatedName.setText(user.name)
        binding.editTextUpdatedLastName.setText(user.lastName)
        binding.switchUpdatedIsAdmin.isChecked = user.isAdmin
        binding.switchUpdatedEnable.isChecked = user.enable
    }

    private fun clearForm() {
        binding.editTextUserId.text.clear()
        binding.editTextUpdatedUserName.text.clear()
        binding.editTextUpdatedName.text.clear()
        binding.editTextUpdatedLastName.text.clear()
        binding.switchUpdatedIsAdmin.isChecked = false
        binding.switchUpdatedEnable.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
