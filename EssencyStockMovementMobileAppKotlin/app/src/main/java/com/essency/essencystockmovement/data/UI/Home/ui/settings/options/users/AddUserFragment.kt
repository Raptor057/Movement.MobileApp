package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AppUser
import com.essency.essencystockmovement.data.repository.AppUserRepository
import com.essency.essencystockmovement.data.repository.WarehouseListRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsUsersAddBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddUserFragment : BaseFragment() {

    private var _binding: FragmentSettingsUsersAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: AppUserRepository
    private lateinit var warehouseRepository: WarehouseListRepository
    private lateinit var userTypeList: List<String> // Lista para el Spinner de UserType

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsUsersAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa la base de datos y los repositorios
        val dbHelper = MyDatabaseHelper(requireContext())
        userRepository = AppUserRepository(dbHelper)
        warehouseRepository = WarehouseListRepository(dbHelper)

        // Cargar lista dinámica de almacenes para el Spinner
        loadUserTypes()

        // Configurar listeners del formulario
        setupFormListeners()

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupFormListeners() {
        binding.buttonSaveUser.setOnClickListener {
            val userName = binding.editTextUserName.text.toString()
            val name = binding.editTextName.text.toString()
            val lastName = binding.editTextLastName.text.toString()
            val password = binding.editTextPassword.text.toString()
            val isAdmin = binding.switchIsAdmin.isChecked
            val isEnabled = binding.switchEnable.isChecked
            val userType = binding.spinnerUserType.selectedItem.toString()

            if (userName.isBlank() || name.isBlank() || lastName.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val newUser = AppUser(
                userName = userName,
                name = name,
                lastName = lastName,
                userType = userType, // Nuevo campo UserType (obtenido del Spinner)
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
        binding.spinnerUserType.setSelection(0) // Reinicia el Spinner a la primera opción
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
