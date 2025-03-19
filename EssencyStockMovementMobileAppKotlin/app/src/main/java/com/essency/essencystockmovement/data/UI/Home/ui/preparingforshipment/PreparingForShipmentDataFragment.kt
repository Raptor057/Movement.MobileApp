package com.essency.essencystockmovement.data.UI.Home.ui.preparingforshipment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.TraceabilityStockList
import com.essency.essencystockmovement.data.repository.AppUserRepository
import com.essency.essencystockmovement.data.repository.TraceabilityStockListRepository
import com.essency.essencystockmovement.databinding.FragmentPreparingForShipmentDataBinding
import com.essency.essencystockmovement.databinding.FragmentReceivingDataBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PreparingForShipmentDataFragment : BaseFragment()
{
    private var _binding: FragmentPreparingForShipmentDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var repository: TraceabilityStockListRepository
    private lateinit var users: AppUserRepository
    private val defaultMovementType: String = "PREPARATION SHIPMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentPreparingForShipmentDataBinding.inflate(inflater, container, false)


        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        users = AppUserRepository(MyDatabaseHelper(requireContext()))
        repository = TraceabilityStockListRepository(MyDatabaseHelper(requireContext()))

        //loadLastTraceabilityStock() // Auto-rellenar (si hay) o limpiar campos
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            saveTraceabilityStock()
        }

        binding.buttonEdit.setOnClickListener {
            enableFields()
        }
    }

    private fun saveTraceabilityStock() {
        val userName = sharedPreferences.getString("userName", "Unknown") ?: "Unknown"
        val pONumber = binding.editTextSourceContainer.text.toString()
        val numberOfHeaters = binding.editTextNumberOfHeaters.text.toString().toIntOrNull() ?: 0

        if (pONumber.isEmpty() || numberOfHeaters == 0) {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener fecha/hora actual en formato ISO 8601
        val timeStamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Obtener datos de usuario (source/destination) según MovementType
        val userData = users.getUserMovementData(userName, defaultMovementType)
        val source = userData?.source ?: "Default Source"
        val destination = userData?.destination ?: "Default Destination"

        val lastStock = repository.getLastInserted(defaultMovementType, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")

        if (lastStock != null && !lastStock.finish) {
            // 🔹 Actualizar la fila existente (todavía no terminada)
            val updatedStock = lastStock.copy(
                batchNumber = pONumber,
                numberOfHeaters = numberOfHeaters,
                timeStamp = timeStamp,
                createdBy = userName
            )

            val rowsUpdated = repository.update(updatedStock)
            if (rowsUpdated > 0) {
                Toast.makeText(requireContext(), "Registro actualizado con éxito!", Toast.LENGTH_SHORT).show()
                // 🔹 Recargar campos desde BD
                loadLastTraceabilityStock()
            } else {
                Toast.makeText(requireContext(), "Error al actualizar.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 🔹 Crear una nueva fila si no hay o la última está finalizada
            val newStock = TraceabilityStockList(
                batchNumber = pONumber,
                movementType = defaultMovementType,
                numberOfHeaters = numberOfHeaters,
                numberOfHeatersFinished = 0,
                finish = false,
                sendByEmail = false,
                createdBy = userName,
                timeStamp = timeStamp,
                notes = "",
                id = 0,
                source = source,
                destination = destination
            )

            val result = repository.insert(newStock, defaultMovementType, userName)
            if (result == -1L) {
                Toast.makeText(requireContext(), "No se puede insertar: Finaliza o completa los calentadores primero", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Registro guardado con éxito!", Toast.LENGTH_SHORT).show()
                // 🔹 Recargar campos desde BD
                loadLastTraceabilityStock()
            }
        }
    }

    private fun loadLastTraceabilityStock() {
        val lastStock = repository.getLastInserted(defaultMovementType, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")

        if (lastStock != null) {
            // Hay un registro en la BD
            binding.editTextMovementType.setText(lastStock.movementType)
            binding.editTextSourceContainer.setText(lastStock.batchNumber)
            binding.editTextNumberOfHeaters.setText(lastStock.numberOfHeaters.toString())
            binding.editTextSource.setText(lastStock.source)
            binding.editTextDestination.setText(lastStock.destination)
        } else {
            // No hay datos previos: dejamos los campos en blanco
            binding.editTextMovementType.setText("")
            binding.editTextSourceContainer.setText("")
            binding.editTextNumberOfHeaters.setText("")
            binding.editTextSource.setText("")
            binding.editTextDestination.setText("")
        }

        // Bloquearlos por defecto
        disableFields()
    }

    private fun enableFields() {
        binding.editTextNumberOfHeaters.isEnabled = true
        binding.editTextSourceContainer.isEnabled = true

        binding.editTextNumberOfHeaters.setBackgroundColor(Color.WHITE)
        binding.editTextSourceContainer.setBackgroundColor(Color.WHITE)

        binding.buttonSave.visibility = View.VISIBLE
        binding.buttonEdit.visibility = View.GONE
    }

    private fun disableFields() {
        binding.editTextNumberOfHeaters.isEnabled = false
        binding.editTextSourceContainer.isEnabled = false

        binding.editTextNumberOfHeaters.setBackgroundColor(Color.WHITE)
        binding.editTextSourceContainer.setBackgroundColor(Color.WHITE)

        binding.buttonSave.visibility = View.GONE
        binding.buttonEdit.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}