package com.essency.essencystockmovement.data.UI.Home.ui.receiving

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.UtilClass.BarcodeParser
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.BarcodeData
import com.essency.essencystockmovement.data.model.StockList
import com.essency.essencystockmovement.data.repository.TraceabilityStockListRepository
import com.essency.essencystockmovement.databinding.FragmentStockListBinding

class ReceivingFragment : BaseFragment() {

    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReceivingAdapter
    private val stockList = mutableListOf<StockList>()
    private lateinit var barcodeParser: BarcodeParser
    private lateinit var repository: TraceabilityStockListRepository
    private lateinit var sharedPreferences: SharedPreferences


    private lateinit var dbHelper: MyDatabaseHelper
    //private var palletRegex: Regex? = null // Expresión regular cargada desde la base de datos

//    val masterKey = MasterKey.Builder(requireContext())
//        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//        .build()
//
//    // Usa exactamente el mismo nombre "EncryptedUserPrefs"
//    val encryptedPrefs = EncryptedSharedPreferences.create(
//        requireContext(),
//        "EncryptedUserPrefs",
//        masterKey,
//        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        barcodeParser = BarcodeParser()
        _binding = FragmentStockListBinding.inflate(inflater, container, false)
        dbHelper = MyDatabaseHelper(requireContext())
        repository = TraceabilityStockListRepository(MyDatabaseHelper(requireContext()))
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // 🔹 Obtener todos los registros de StockList que coincidan con el último TraceabilityStockList
        stockList.addAll(getStockListForLastTraceability())

        binding.editTextNewStockItem.setBackgroundColor(Color.WHITE)

        // Asegura que el EditText tenga el foco
        binding.editTextNewStockItem.requestFocus()
        setupRecyclerView()
        setupTextInputValidation()

        stockList.addAll(getStockListForLastTraceability())

        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextNewStockItem.windowToken, 0)

        // Ahora puedes leer datos como si fuera un SharedPreferences normal
        //val userName = encryptedPrefs.getString("userName", "Unknown")
        //val source = encryptedPrefs.getString("source", "Desconocido")
        //val destination = encryptedPrefs.getString("destination", "Desconocido")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Al volver de otra pantalla, recupera el foco
        binding.editTextNewStockItem.requestFocus()
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
                        // convertToStockList puede retornar 1 o 2 StockList
                        val stockItems = convertToStockList(parsedData)

                        for (item in stockItems) {
                            val insertedId = insertNewStockItem(item)
                            if (insertedId != -1L) {
                                // Copiar el item con el nuevo ID
                                val itemWithId = item.copy(id = insertedId.toInt())
                                stockList.add(itemWithId)
                                adapter.notifyDataSetChanged()
                            } else {
                                binding.editTextNewStockItem.error = "Error inserting item"
                            }
//                            val insertedId = insertNewStockItem(item)
//                            if (insertedId != -1L) {
//                                stockList.add(item)
//                            } else {
//                                binding.editTextNewStockItem.error = "Error inserting item"
//                            }
                        }
                        adapter.notifyDataSetChanged()
                        binding.editTextNewStockItem.text.clear()
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

        val insertedId = db.insert("StockList", null, values)
        db.close()
        return insertedId
//        val id = db.insert("StockList", null, values)
//        db.close()
//        return id
    }

    private fun getStockListForLastTraceability(): List<StockList> {
        val db = dbHelper.readableDatabase
        val stockList = mutableListOf<StockList>()

        // 🔹 Obtener el último `IDTraceabilityStockList`
        val lastTraceabilityStock = repository.getLastInserted()
        val traceabilityId = lastTraceabilityStock?.id ?: return emptyList() // Si no hay ID, retorna lista vacía

        val query = "SELECT * FROM StockList WHERE IDTraceabilityStockList = ? ORDER BY ID DESC"
        val cursor = db.rawQuery(query, arrayOf(traceabilityId.toString()))

        cursor.use {
            while (it.moveToNext()) {
                stockList.add(cursorToStock(it))
            }
        }

        return stockList
    }



//    private fun convertToStockList(parsedData: BarcodeData): StockList {
//        val lastStock = getLastInserted() // Obtener la última entrada para completar datos
//        val lastTraceabilityStock = repository.getLastInserted()
//        return StockList(
//            id = 0, // Se autogenerará en la BD
//            idTraceabilityStockList = lastTraceabilityStock?.id ?: 0, // Usar el último o 0 si no hay
//            company = parsedData.countryOfProduction ?: parsedData.countryOfProductionWH1 ?: "001", // 🔹 AHORA viene de `CountryOfProduction`
//            source = lastTraceabilityStock?.source ?: "Unknown Source",
//            sourceLoc = "Unknown Source",
//            destination = lastTraceabilityStock?.destination ?: "Unknown Destination",
//            destinationLoc = "Unknown Source",
//            pallet = parsedData.pallet,
//            partNo = parsedData.partNumber ?: parsedData.partNumberWH1 ?: "",
//            rev = parsedData.rev ?: parsedData.revWH1 ?: "",
//            lot = lastTraceabilityStock?.batchNumber ?: "N/A", // 🔹 Ahora `lot` viene de `BatchNumber` de `TraceabilityStockList`
//            qty = parsedData.countOfTradeItems ?: parsedData.countOfTradeItemsWH1 ?: 1,
//            productionDate = parsedData.productionDate ?: parsedData.productionDateWH1 ?: "",
//            countryOfProduction = parsedData.countryOfProduction ?: parsedData.countryOfProductionWH1 ?: "",
//            serialNumber = parsedData.serialNumber ?: parsedData.serialNumberWH1 ?: "",
//            date = "2024-02-06", // Fecha actual o extraída
//            timeStamp = System.currentTimeMillis().toString(), // Timestamp actual
//            //user = sharedPreferences?.getString("userName", "Unknown User") ?: "Unknown User",
//            user = sharedPreferences.getString("userName", "Unknown User") ?: "Unknown User",
//            contBolNum = "${lastTraceabilityStock?.batchNumber ?: "N/A"}-${(lastStock?.id ?: 0) + 1}"
//        )
//    }

    private fun convertToStockList(parsedData: BarcodeData): List<StockList> {
        val lastTraceability = repository.getLastInserted()
        val traceId = lastTraceability?.id ?: 0

        // Función para crear un StockList individual según los parámetros
        @RequiresApi(Build.VERSION_CODES.O)
        fun buildStock(
            partNumber: String?,
            rev: String?,
            count: Int?,
            pallet: String?,
            productionDate: String?,
            country: String?,
            serial: String?
        ): StockList {
            return StockList(
                id = 0,
                idTraceabilityStockList = traceId,
                // Por simplicidad, omito el resto de campos, llénalos como necesites
                company = country ?: "001",
                source = lastTraceability?.source ?: "Unknown",
                sourceLoc = "Unknown Source",
                destination = lastTraceability?.destination ?: "Unknown Destination",
                destinationLoc = "Unknown",
                pallet = pallet,
                partNo = partNumber ?: "",
                rev = rev ?: "",
                lot = lastTraceability?.batchNumber ?: "N/A",
                qty = count ?: 1,
                productionDate = productionDate ?: "",
                countryOfProduction = country ?: "",
                serialNumber = serial ?: "",
                date = "2024-02-06",//Localdatetime.now().toString(),
                timeStamp = System.currentTimeMillis().toString(),
                user = sharedPreferences.getString("userName", "Unknown") ?: "Unknown", //encryptedPrefs.getString("userName", "Unknown").toString(),//sharedPreferences.getString("userName", "Unknown") ?: "Unknown",
                contBolNum = "${lastTraceability?.batchNumber ?: "N/A"}-XYZ" // Ajusta la lógica
            )
        }

        return when {
            // Caso 1: Dos calentadores (pallet + partNumberWH2 != null)
            parsedData.pallet != null && parsedData.partNumberWH2 != null -> {
                val item1 = buildStock(
                    parsedData.partNumberWH1,
                    parsedData.revWH1,
                    parsedData.countOfTradeItemsWH1,
                    parsedData.pallet,
                    parsedData.productionDateWH1,
                    parsedData.countryOfProductionWH1,
                    parsedData.serialNumberWH1
                )
                val item2 = buildStock(
                    parsedData.partNumberWH2,
                    parsedData.revWH2,
                    parsedData.countOfTradeItemsWH2,
                    parsedData.pallet,
                    parsedData.productionDateWH2,
                    parsedData.countryOfProductionWH2,
                    parsedData.serialNumberWH2
                )
                listOf(item1, item2)
            }

            // Caso 2: un solo calentador sin pallet (pallet == null), usando “partNumber” normal
            parsedData.pallet == null && parsedData.partNumber != null -> {
                val singleItem = buildStock(
                    parsedData.partNumber,
                    parsedData.rev,
                    parsedData.countOfTradeItems,
                    null,
                    parsedData.productionDate,
                    parsedData.countryOfProduction,
                    parsedData.serialNumber
                )
                listOf(singleItem)
            }

            // Caso 3: un solo calentador con pallet pero sin partNumberWH2 (ej. un “WH1”)
            parsedData.pallet != null && parsedData.partNumberWH2 == null -> {
                val singleItem = buildStock(
                    parsedData.partNumberWH1,
                    parsedData.revWH1,
                    parsedData.countOfTradeItemsWH1,
                    parsedData.pallet,
                    parsedData.productionDateWH1,
                    parsedData.countryOfProductionWH1,
                    parsedData.serialNumberWH1
                )
                listOf(singleItem)
            }

            // Caso 4: otra variante o error
            else -> emptyList() // Devuelve lista vacía si no hay datos
        }
    }



    private fun setupRecyclerView() {
        adapter = ReceivingAdapter(stockList) { itemToDelete ->
            removeStockItem(itemToDelete)  // <- Aquí definimos la acción de borrar
        }
        binding.recyclerViewStockList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStockList.adapter = adapter
    }

//    private fun removeStockItem(stockItem: StockList) {
//        stockList.remove(stockItem)
//        adapter.notifyDataSetChanged()
//    }

private fun removeStockItem(item: StockList) {
    // 1) Eliminar de la BD usando tu repositorio
    val rowsDeleted = dbHelper.writableDatabase.delete(
        "StockList",
        "ID = ?",
        arrayOf(item.id.toString())
    )

    // 2) Si rowsDeleted > 0, lo quitas de la lista en memoria
    if (rowsDeleted > 0) {
        stockList.remove(item)
        adapter.notifyDataSetChanged()
    } else {
        // Muestra un error o algo si no se pudo borrar
    }
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
