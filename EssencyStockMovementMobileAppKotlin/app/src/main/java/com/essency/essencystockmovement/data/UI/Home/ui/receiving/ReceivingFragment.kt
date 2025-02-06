package com.essency.essencystockmovement.data.UI.Home.ui.receiving

import android.content.ContentValues
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.UtilClass.BarcodeParser
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.BarcodeData
import com.essency.essencystockmovement.data.model.StockList
import com.essency.essencystockmovement.databinding.FragmentStockListBinding


class ReceivingFragment : BaseFragment() {

    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReceivingAdapter
    private val stockList = mutableListOf<StockList>()
    private lateinit var barcodeParser: BarcodeParser


    private lateinit var dbHelper: MyDatabaseHelper
    private var palletRegex: Regex? = null // Expresión regular cargada desde la base de datos

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        barcodeParser = BarcodeParser()
        _binding = FragmentStockListBinding.inflate(inflater, container, false)
        dbHelper = MyDatabaseHelper(requireContext())

        // 🔹 Obtener el último registro insertado en StockList
        val lastStock = getLastInserted()

        if (lastStock != null) {
            // 🔹 Muestra los datos en el RecyclerView o en la UI
            stockList.add(lastStock)
        }
        binding.editTextNewStockItem.setBackgroundColor(Color.DKGRAY)
        setupRecyclerView()

        return binding.root
    }

    private fun getLastInserted(): StockList? {
        val db = dbHelper.readableDatabase
        var lastStock: StockList? = null
        val query = "SELECT * FROM StockList ORDER BY ID DESC LIMIT 1"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            if (it.moveToFirst()) {
                lastStock = cursorToStock(it)
            }
        }

        return lastStock
    }

    private fun setupTextInputValidation() {
        binding.editTextNewStockItem.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            // Detectar "Enter" desde el teclado físico o botón en pantalla
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {

                val input = binding.editTextNewStockItem.text.toString().trim()

                if (input.isNotEmpty()) {
                    val parsedData = barcodeParser.parseBarcode(input)

                    if (parsedData != null) {
                        // 🔹 Convertir los datos extraídos a StockList
                        val newStockItem = convertToStockList(parsedData)

                        // 🔹 Insertar en la base de datos
                        val insertedId = insertNewStockItem(newStockItem)

                        if (insertedId != -1L) {
                            stockList.add(newStockItem)
                            adapter.notifyDataSetChanged()
                            binding.editTextNewStockItem.text.clear() // 🔹 Limpiar el input después de la inserción
                        }
                    } else {
                        binding.editTextNewStockItem.error = "Invalid barcode format!"
                    }
                }

                return@OnEditorActionListener true // Indicar que el evento fue manejado
            }
            false
        })
    }

    private fun insertNewStockItem(stockItem: StockList): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IDTraceabilityStockList", stockItem.idTraceabilityStockList)
            put("Company", stockItem.company)
            put("Source", stockItem.source)
            put("SourceLoc", stockItem.sourceLoc)
            put("Destination", stockItem.destination)
            put("DestinationLoc", stockItem.destinationLoc)
            put("Pallet", stockItem.pallet)
            put("PartNo", stockItem.partNo)
            put("Rev", stockItem.rev)
            put("Lot", stockItem.lot)
            put("Qty", stockItem.qty)
            put("ProductionDate", stockItem.productionDate)
            put("CountryOfProduction", stockItem.countryOfProduction)
            put("SerialNumber", stockItem.serialNumber)
            put("Date", stockItem.date)
            put("TimeStamp", stockItem.timeStamp)
            put("User", stockItem.user)
            put("ContBolNum", stockItem.contBolNum)
        }

        val id = db.insert("StockList", null, values)
        db.close()
        return id
    }


    private fun convertToStockList(parsedData: BarcodeData): StockList {
        val lastStock = getLastInserted() // Obtener la última entrada para completar datos

        return StockList(
            id = 0, // Se autogenerará en la BD
            idTraceabilityStockList = lastStock?.idTraceabilityStockList ?: 0, // Usar el último o 0 si no hay
            company = lastStock?.company ?: "Default Company",
            source = lastStock?.source ?: "Unknown Source",
            sourceLoc = lastStock?.sourceLoc,
            destination = lastStock?.destination ?: "Unknown Destination",
            destinationLoc = lastStock?.destinationLoc,
            pallet = parsedData.pallet,
            partNo = parsedData.partNumber ?: parsedData.partNumberWH1 ?: "",
            rev = parsedData.rev ?: parsedData.revWH1 ?: "",
            lot = "N/A", // No se extrae de la regex
            qty = parsedData.countOfTradeItems ?: parsedData.countOfTradeItemsWH1 ?: 1,
            productionDate = parsedData.productionDate ?: parsedData.productionDateWH1 ?: "",
            countryOfProduction = parsedData.countryOfProduction ?: parsedData.countryOfProductionWH1 ?: "",
            serialNumber = parsedData.serialNumber ?: parsedData.serialNumberWH1 ?: "",
            date = "2024-02-06", // Fecha actual o extraída
            timeStamp = System.currentTimeMillis().toString(), // Timestamp actual
            user = lastStock?.user ?: "Unknown User",
            contBolNum = lastStock?.contBolNum ?: "N/A"
        )
    }


    private fun setupRecyclerView() {
        adapter = ReceivingAdapter(stockList) { itemToDelete ->
            removeStockItem(itemToDelete)
        }
        binding.recyclerViewStockList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStockList.adapter = adapter
    }

    private fun removeStockItem(stockItem: StockList) {
        stockList.remove(stockItem)
        adapter.notifyDataSetChanged()
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
