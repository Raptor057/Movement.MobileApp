package com.essency.essencystockmovement.data.UI.Home.ui.preparingforshipment

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.UI.Home.ui.receiving.ReceivingAdapter
import com.essency.essencystockmovement.data.UtilClass.BarcodeParser
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.StockList
import com.essency.essencystockmovement.data.repository.MovementTypeRepository
import com.essency.essencystockmovement.data.repository.TraceabilityStockListRepository
import com.essency.essencystockmovement.databinding.FragmentPreparingForShipmentBinding
import com.essency.essencystockmovement.databinding.FragmentStockListBinding
import com.essency.essencystockmovement.databinding.FragmentStockListPreparingForShipmentBinding

class PreparingForShipmentFragment : BaseFragment() {

    private var _binding: FragmentStockListPreparingForShipmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReceivingAdapter
    private val stockList = mutableListOf<StockList>()
    private lateinit var barcodeParser: BarcodeParser
    private lateinit var repository: TraceabilityStockListRepository
    private lateinit var movementType: MovementTypeRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: MyDatabaseHelper
    private val defaultMovementType: String = "PREPARATION SHIPMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        barcodeParser = BarcodeParser()
        _binding = FragmentStockListPreparingForShipmentBinding.inflate(inflater, container, false)
        dbHelper = MyDatabaseHelper(requireContext())
        repository = TraceabilityStockListRepository(MyDatabaseHelper(requireContext()))
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        movementType = MovementTypeRepository(MyDatabaseHelper(requireContext())) //Agregado para obtener el destino segun el modulo

        // 🔹 Inicializa el adapter antes de usar `stockList`
        setupRecyclerView()

        // 🔹 Ahora carga los datos en `stockList`
        //stockList.addAll(getStockListForLastTraceability())
        adapter.notifyDataSetChanged()

        // 🔹 Actualiza el contador de entrada
        //updateCounterUI()

        binding.editTextNewStockItem.setBackgroundColor(Color.WHITE)
        binding.editTextNewStockItem.requestFocus()
        //setupTextInputValidation()
        return binding.root
    }


    private fun setupRecyclerView() {
        adapter = ReceivingAdapter(stockList) { itemToDelete ->
            removeStockItem(itemToDelete)  // <- Aquí definimos la acción de borrar
        }
        binding.recyclerViewStockList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStockList.adapter = adapter
    }

    private fun removeStockItem(item: StockList) {
        // Eliminar de la base de datos
        val rowsDeleted = dbHelper.writableDatabase.delete(
            "StockList",
            "ID = ?",
            arrayOf(item.id.toString())
        )

        if (rowsDeleted > 0) {
            // Remover el ítem de la lista en memoria y notificar al adapter
            stockList.remove(item)
            adapter.notifyDataSetChanged()

            // Actualizar contador
            updateCounterUI()

            // Actualizar el registro de trazabilidad según la nueva cantidad de piezas escaneadas
            val lastTraceability = repository.getLastInserted()
            if (lastTraceability != null) {
                val scannedItems = getStockListForLastTraceability()
                val scannedCount = scannedItems.size

                // Si se elimina una pieza y el total es menor que el esperado, se desmarca la finalización.
                val updatedTraceability = if (scannedCount < lastTraceability.numberOfHeaters) {
                    lastTraceability.copy(
                        finish = false,
                        numberOfHeatersFinished = scannedCount
                    )
                } else {
                    // Si aún se cumple o se supera, actualizamos solo el contador.
                    lastTraceability.copy(numberOfHeatersFinished = scannedCount)
                }
                repository.update(updatedTraceability)
            }
        } else {
            // Mostrar un mensaje de error si no se pudo borrar
            Toast.makeText(requireContext(), "Error al borrar el ítem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCounterUI() {
        val lastTraceability = repository.getLastInserted()

        if (lastTraceability == null) {
            // Si no existe ningún registro, mostramos 0/0
            binding.textViewCounter.text = "Calentadores: 0 / 0"
            return
        }

        if (lastTraceability.finish) {
            // Si el último lote ya está finalizado, también mostramos 0/0
            binding.textViewCounter.text = "Calentadores: 0 / 0"
            return
        }

        // Si el lote no está finalizado, calculamos cuántos lleva
        val scannedCount = getStockListForLastTraceability().size
        val totalHeaters = lastTraceability.numberOfHeaters
        binding.textViewCounter.text = "Calentadores: $scannedCount / $totalHeaters"
    }

    private fun getStockListForLastTraceability(): List<StockList> {
        val db = dbHelper.readableDatabase
        val stockList = mutableListOf<StockList>()

        // 🔹 Obtener el último `IDTraceabilityStockList`
        val lastTraceabilityStock = repository.getLastInserted()
        val traceabilityId = lastTraceabilityStock?.id ?: return emptyList() // Si no hay ID, retorna lista vacía

        //val query = "SELECT * FROM StockList WHERE IDTraceabilityStockList = ? ORDER BY ID DESC"
        val query = "SELECT SL.* FROM StockList SL INNER JOIN TraceabilityStockList TSL ON SL.IDTraceabilityStockList = TSL.ID WHERE SL.IDTraceabilityStockList = ? ORDER BY SL.ID DESC"
        val cursor = db.rawQuery(query, arrayOf(traceabilityId.toString()))

        cursor.use {
            while (it.moveToNext()) {
                stockList.add(cursorToStock(it))
            }
        }

        return stockList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cursorToStock(cursor: Cursor): StockList {
        return StockList(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
            idTraceabilityStockList = cursor.getInt(cursor.getColumnIndexOrThrow("IDTraceabilityStockList")),
            company = cursor.getString(cursor.getColumnIndexOrThrow("Company")),
            source = cursor.getString(cursor.getColumnIndexOrThrow("Source")),
            sourceLoc = cursor.getColumnIndex("SourceLoc").takeIf { it != -1 }?.let { cursor.getString(it) },
            destination = cursor.getString(cursor.getColumnIndexOrThrow("Destination")),
            destinationLoc = cursor.getColumnIndex("DestinationLoc").takeIf { it != -1 }?.let { cursor.getString(it) },
            pallet = cursor.getColumnIndex("Pallet").takeIf { it != -1 }?.let { cursor.getString(it) },
            partNo = cursor.getString(cursor.getColumnIndexOrThrow("PartNo")),
            rev = cursor.getString(cursor.getColumnIndexOrThrow("Rev")),
            lot = cursor.getString(cursor.getColumnIndexOrThrow("Lot")),
            qty = cursor.getInt(cursor.getColumnIndexOrThrow("Qty")),
            productionDate = cursor.getColumnIndex("ProductionDate").takeIf { it != -1 }?.let { cursor.getString(it) },
            countryOfProduction = cursor.getColumnIndex("CountryOfProduction").takeIf { it != -1 }?.let { cursor.getString(it) },
            serialNumber = cursor.getColumnIndex("SerialNumber").takeIf { it != -1 }?.let { cursor.getString(it) },
            date = cursor.getString(cursor.getColumnIndexOrThrow("Date")),
            timeStamp = cursor.getString(cursor.getColumnIndexOrThrow("TimeStamp")),
            user = cursor.getString(cursor.getColumnIndexOrThrow("User")),
            contBolNum = cursor.getString(cursor.getColumnIndexOrThrow("ContBolNum"))
        )
    }
}