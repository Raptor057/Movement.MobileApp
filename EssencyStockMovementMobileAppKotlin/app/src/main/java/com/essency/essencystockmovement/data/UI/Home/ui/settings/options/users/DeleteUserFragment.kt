package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.repository.AppUserRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsUsersDeleteBinding

class DeleteUserFragment : BaseFragment() { //Fragment() {

    private var _binding: FragmentSettingsUsersDeleteBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: AppUserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsUsersDeleteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa la base de datos y el repositorio
        val dbHelper = MyDatabaseHelper(requireContext())
        userRepository = AppUserRepository(dbHelper)

        setupListeners()

        return root
    }

    private fun setupListeners() {
        // Botón para eliminar usuario por ID
        binding.buttonDeleteUser.setOnClickListener {
            val userId = binding.editTextUserId.text.toString().toIntOrNull()
            if (userId == null) {
                Toast.makeText(requireContext(), "Ingresa un ID válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rowsDeleted = userRepository.deleteById(userId)
            if (rowsDeleted > 0) {
                Toast.makeText(requireContext(), "Usuario eliminado correctamente.", Toast.LENGTH_SHORT).show()
                clearForm()
            } else {
                Toast.makeText(requireContext(), "No se encontró un usuario con ese ID.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearForm() {
        binding.editTextUserId.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}