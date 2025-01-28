package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.warehouse

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.WarehouseList
import com.essency.essencystockmovement.data.repository.WarehouseListRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsWarehouseGetBinding

class GetWarehouseFragment : BaseFragment() {

    private var _binding: FragmentSettingsWarehouseGetBinding? = null
    private val binding get() = _binding!!

    private lateinit var warehouseRepository: WarehouseListRepository
    private lateinit var adapter: WarehouseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsWarehouseGetBinding.inflate(inflater, container, false)
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
            onEditClicked = { warehouse ->
                Toast.makeText(
                    requireContext(),
                    "Editing not available in this section.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.recyclerViewWarehouses.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewWarehouses.adapter = adapter

        // Cargar la lista inicial de almacenes
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
        if (warehouses.isEmpty()) {
            Toast.makeText(requireContext(), "No warehouses available.", Toast.LENGTH_SHORT).show()
        } else {
            adapter.updateData(warehouses)
        }
    }

    private fun refreshData() {
        // Recargar la lista desde el repositorio
        val warehouses = warehouseRepository.getAllWarehouses()
        adapter.updateData(warehouses)

        // Ocultar el indicador de "refresh"
        binding.swipeRefreshLayout.isRefreshing = false

        Toast.makeText(requireContext(), "Data refreshed!", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Recargar los datos automáticamente cada vez que el fragmento sea visible
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
