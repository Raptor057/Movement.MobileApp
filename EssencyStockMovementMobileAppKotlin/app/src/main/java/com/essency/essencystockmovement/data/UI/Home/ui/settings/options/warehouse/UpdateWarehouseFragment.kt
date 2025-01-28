package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.warehouse

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.WarehouseList
import com.essency.essencystockmovement.data.repository.WarehouseListRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsWarehouseUpdateBinding

class UpdateWarehouseFragment : BaseFragment() {

    private var _binding: FragmentSettingsWarehouseUpdateBinding? = null
    private val binding get() = _binding!!

    private lateinit var warehouseRepository: WarehouseListRepository
    private lateinit var adapter: WarehouseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsWarehouseUpdateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializar el repositorio
        val dbHelper = MyDatabaseHelper(requireContext())
        warehouseRepository = WarehouseListRepository(dbHelper)

        setupRecyclerView()
        setupFilter()
        setupSwipeToRefresh()

        return root
    }

    private fun setupRecyclerView() {
        adapter = WarehouseAdapter(
            warehouses = mutableListOf(),
            onEditClicked = { warehouse -> showEditDialog(warehouse) }
        )

        binding.recyclerViewWarehouses.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewWarehouses.adapter = adapter

        // Cargar todos los almacenes al inicio
        loadWarehouses()
    }

    private fun setupFilter() {
        binding.editTextWarehouseFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                val filteredWarehouses = warehouseRepository.getAllWarehouses()
                    .filter { it.warehouse.contains(query, ignoreCase = true) }
                adapter.updateData(filteredWarehouses)
            }
        })
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun loadWarehouses() {
        val warehouses = warehouseRepository.getAllWarehouses()
        adapter.updateData(warehouses)
    }

    private fun refreshData() {
        val warehouses = warehouseRepository.getAllWarehouses()
        adapter.updateData(warehouses)

        // Ocultar el indicador de "refresh"
        binding.swipeRefreshLayout.isRefreshing = false

        Toast.makeText(requireContext(), "Data refreshed!", Toast.LENGTH_SHORT).show()
    }

    private fun showEditDialog(warehouse: WarehouseList) {
        // Mostrar el layout de edición
        binding.layoutEditWarehouse.visibility = View.VISIBLE
        binding.editTextWarehouseName.setText(warehouse.warehouse)

        binding.buttonUpdateWarehouse.setOnClickListener {
            val updatedName = binding.editTextWarehouseName.text.toString()

            if (updatedName.isBlank()) {
                Toast.makeText(requireContext(), "Warehouse name cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val updatedWarehouse = warehouse.copy(warehouse = updatedName)
                warehouseRepository.updateWarehouse(updatedWarehouse)

                Toast.makeText(requireContext(), "Warehouse updated successfully!", Toast.LENGTH_SHORT).show()
                binding.layoutEditWarehouse.visibility = View.GONE
                refreshData()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error updating warehouse: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
