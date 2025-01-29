package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.UtilClass.PBKDF2Helper
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AppUser
import com.essency.essencystockmovement.data.repository.AppUserRepository
import com.essency.essencystockmovement.data.repository.WarehouseListRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsUsersUpdateBinding

class UpdateUserFragment : BaseFragment() {

    private var _binding: FragmentSettingsUsersUpdateBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: AppUserRepository
    private lateinit var warehouseRepository: WarehouseListRepository
    private lateinit var userTypeList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsUsersUpdateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa la base de datos y los repositorios
        val dbHelper = MyDatabaseHelper(requireContext())
        userRepository = AppUserRepository(dbHelper)
        warehouseRepository = WarehouseListRepository(dbHelper)

        // Cargar lista dinámica de almacenes para el Spinner
        loadUserTypes()

        setupListeners()

        return root
    }

    private fun loadUserTypes() {
        // Obtener la lista de almacenes desde la base de datos
        val warehouses = warehouseRepository.getAllWarehouses()

        // Si no hay almacenes, usa una lista de respaldo
        userTypeList = if (warehouses.isNotEmpty()) {
            warehouses.map { it.warehouse } // Extraer solo los nombres de los almacenes
        } else {
            listOf("No warehouses available") // Mensaje de respaldo si la lista está vacía
        }

        setupSpinner()
    }

    private fun setupSpinner() {
        // Configurar el adaptador para el Spinner con la lista de almacenes obtenida
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userTypeList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUserType.adapter = adapter
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
            val userType = binding.spinnerUserType.selectedItem.toString() // Obtener el valor seleccionado del Spinner

            val updatedUser = AppUser(
                id = userId,
                userName = binding.editTextUpdatedUserName.text.toString(),
                name = binding.editTextUpdatedName.text.toString(),
                lastName = binding.editTextUpdatedLastName.text.toString(),
                userType = userType, // Nuevo campo UserType (obtenido del Spinner)
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

        // Establecer el valor del Spinner en función del userType guardado
        val position = userTypeList.indexOf(user.userType)
        if (position >= 0) {
            binding.spinnerUserType.setSelection(position)
        }
    }

    private fun clearForm() {
        binding.editTextUserId.text.clear()
        binding.editTextUpdatedUserName.text.clear()
        binding.editTextUpdatedName.text.clear()
        binding.editTextUpdatedLastName.text.clear()
        binding.switchUpdatedIsAdmin.isChecked = false
        binding.switchUpdatedEnable.isChecked = true
        binding.spinnerUserType.setSelection(0) // Reinicia el Spinner a la primera opción
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
