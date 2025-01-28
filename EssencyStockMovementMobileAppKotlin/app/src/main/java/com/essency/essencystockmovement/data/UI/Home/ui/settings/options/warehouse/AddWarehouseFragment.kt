package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.warehouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.WarehouseList
import com.essency.essencystockmovement.data.repository.WarehouseListRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsWarehouseAddBinding

class AddWarehouseFragment : BaseFragment() {

    private var _binding: FragmentSettingsWarehouseAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var warehouseRepository: WarehouseListRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsWarehouseAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa el repositorio
        val dbHelper = MyDatabaseHelper(requireContext())
        warehouseRepository = WarehouseListRepository(dbHelper)

        setupUI()

        return root
    }

    private fun setupUI() {
        // Configurar el botón para agregar un nuevo almacén
        binding.buttonSaveWarehouse.setOnClickListener {
            val warehouseName = binding.editTextWarehouseName.text.toString()

            if (warehouseName.isBlank()) {
                Toast.makeText(requireContext(), "Warehouse name cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val newWarehouse = WarehouseList(id = 0, warehouse = warehouseName)
                warehouseRepository.insertWarehouse(newWarehouse)

                Toast.makeText(requireContext(), "Warehouse added successfully!", Toast.LENGTH_SHORT).show()

                // Limpiar el campo después de guardar
                binding.editTextWarehouseName.text.clear()

            } catch (e: IllegalArgumentException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
