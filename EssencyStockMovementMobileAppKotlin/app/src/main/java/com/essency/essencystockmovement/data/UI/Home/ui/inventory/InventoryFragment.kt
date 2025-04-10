//package com.essency.essencystockmovement.data.UI.Home.ui.inventory
//
//import android.annotation.SuppressLint
//import android.content.ContentValues
//import android.content.Context
//import android.content.SharedPreferences
//import android.database.Cursor
//import android.graphics.Color
//import android.os.Build
//import android.os.Bundle
//import android.view.KeyEvent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.inputmethod.EditorInfo
//import android.widget.TextView
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.essency.essencystockmovement.data.UI.BaseFragment
//import com.essency.essencystockmovement.data.UI.Home.ui.receiving.ReceivingAdapter
//import com.essency.essencystockmovement.data.UtilClass.BarcodeParser
//import com.essency.essencystockmovement.data.UtilClass.EmailSenderService
//import com.essency.essencystockmovement.data.local.MyDatabaseHelper
//import com.essency.essencystockmovement.data.model.BarcodeData
//import com.essency.essencystockmovement.data.model.StockList
//import com.essency.essencystockmovement.data.model.TraceabilityStockList
//import com.essency.essencystockmovement.data.repository.AuditTraceabilityStockListRepository
//import com.essency.essencystockmovement.data.repository.EmailRepository
//import com.essency.essencystockmovement.data.repository.EmailSenderRepository
//import com.essency.essencystockmovement.data.repository.MovementTypeRepository
//import com.essency.essencystockmovement.data.repository.TraceabilityStockListRepository
//import com.essency.essencystockmovement.databinding.FragmentStockListAuditBinding
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.text.SimpleDateFormat
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//import java.util.Date
//
//class InventoryFragment : BaseFragment() {
//
//    private var _binding: FragmentStockListAuditBinding? = null
//
//    private val binding get() = _binding!!
//
//    private lateinit var adapter: ReceivingAdapter
//    private val stockList = mutableListOf<StockList>()
//    private lateinit var barcodeParser: BarcodeParser
//    private lateinit var traceabilityStockListRepository: TraceabilityStockListRepository
//    private lateinit var movementType: MovementTypeRepository
//    private lateinit var sharedPreferences: SharedPreferences
//    private var moduleName = "INVENTARIO"
//
//
//    private lateinit var dbHelper: MyDatabaseHelper
//
//    @SuppressLint("NotifyDataSetChanged")
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View {
//        barcodeParser = BarcodeParser()
//        _binding = FragmentStockListAuditBinding.inflate(inflater, container, false)
//        dbHelper = MyDatabaseHelper(requireContext())
//        traceabilityStockListRepository = TraceabilityStockListRepository(MyDatabaseHelper(requireContext()))
//        traceabilityStockListRepository = TraceabilityStockListRepository(MyDatabaseHelper(requireContext()))
//        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//        movementType = MovementTypeRepository(MyDatabaseHelper(requireContext())) //Agregado para obtener el destino segun el modulo
//
//        // 🔹 Inicializa el adapter antes de usar `stockList`
//        setupRecyclerView()
//
//        // 🔹 Ahora carga los datos en `stockList`
//        stockList.addAll(getStockListForLastTraceability())
//        adapter.notifyDataSetChanged()
//
//        // 🔹 Actualiza el contador de entrada
//        updateCounterUI()
//
//        binding.editTextNewStockItem.setBackgroundColor(Color.WHITE)
//        binding.editTextNewStockItem.requestFocus()
//        setupTextInputValidation()
//
//        binding.btnMail.setOnClickListener {
//            finalizeRecordAndSendEmail()
//        }
//        return binding.root
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Al volver de otra pantalla, recupera el foco
//        binding.editTextNewStockItem.requestFocus()
//    }
//
//    private fun setupTextInputValidation() {
//        binding.editTextNewStockItem.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_DONE ||
//                actionId == EditorInfo.IME_ACTION_NEXT ||
//                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
//
//                val userName = sharedPreferences.getString("userName", "Unknown") ?: "Unknown"
//                // Obtener o crear registro activo
//                var currentTraceability = traceabilityStockListRepository.getLastInserted(moduleName, userName)
//                if (currentTraceability == null || currentTraceability.finish) {
//                    // No hay registro activo o ya está finalizado: crear uno nuevo sin límite
//                    val timeStamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
//                    else "00:00:00"
//                    val source = movementType.getSourceInMovementTypesByTypeandUserType(moduleName, sharedPreferences.getString("userType", "Unknown") ?: "Unknown")
//                    val destination = movementType.getDestinationInMovementTypesByTypeandUserType(moduleName, sharedPreferences.getString("userType", "Unknown") ?: "Unknown")
//                    val newRecord = TraceabilityStockList(
//                        batchNumber = "",  // Se puede actualizar con el primer valor escaneado si así lo prefieres
//                        movementType = moduleName,
//                        numberOfHeaters = 0,  // Sin límite
//                        numberOfHeatersFinished = 0,
//                        finish = false,
//                        sendByEmail = false,
//                        createdBy = userName,
//                        timeStamp = timeStamp,
//                        notes = "",
//                        id = 0,
//                        source = source,
//                        destination = destination
//                    )
//                    traceabilityStockListRepository.insert(newRecord, moduleName, userName)
//                    currentTraceability = traceabilityStockListRepository.getLastInserted(moduleName, userName)
//                }
//
//                // Ya tenemos un registro activo; continuamos procesando el barcode
//                val input = binding.editTextNewStockItem.text.toString().trim()
//                if (input.isNotEmpty()) {
//                    val parsedData = barcodeParser.parseBarcode(input)
//                    if (parsedData != null) {
//                        val scannedSerial = parsedData.serialNumberWH1
//                            ?: parsedData.serialNumberWH2
//                            ?: parsedData.serialNumber
//
//                        // Evitar duplicados
//                        val duplicate = stockList.any { it.serialNumber == scannedSerial }
//                        if (duplicate) {
//                            binding.editTextNewStockItem.error = "Este dato ya fue escaneado"
//                            return@OnEditorActionListener true
//                        }
//                        val stockItems = convertToStockList(parsedData)
//                        // Ya no se verifica límite, se agregan los ítems directamente
//                        for (item in stockItems) {
//                            val insertedId = insertNewStockItem(item)
//                            if (insertedId != -1L) {
//                                val itemWithId = item.copy(id = insertedId.toInt())
//                                stockList.add(itemWithId)
//                                adapter.notifyItemInserted(stockList.size - 1)
//                            } else {
//                                binding.editTextNewStockItem.error = "Error al insertar ítem"
//                            }
//                        }
//                        adapter.notifyDataSetChanged()
//                        // Actualiza contador (opcional, aquí se puede simplemente reflejar la cantidad escaneada)
//                        updateCounterUI()
//                        binding.editTextNewStockItem.text.clear()
//                    } else {
//                        binding.editTextNewStockItem.error = "Formato de código inválido"
//                    }
//                }
//                return@OnEditorActionListener true
//            }
//            false
//        })
//    }
//
//
//    private fun insertNewStockItem(stockItem: StockList): Long {
//        val db = dbHelper.writableDatabase
//        val values = ContentValues().apply {
//            put("IDTraceabilityStockList", stockItem.idTraceabilityStockList)
//            put("Company", stockItem.company)
//            put("Source", stockItem.source)
//            put("SourceLoc", stockItem.sourceLoc)
//            put("Destination", stockItem.destination)
//            put("DestinationLoc", stockItem.destinationLoc)
//            put("Pallet", stockItem.pallet)
//            put("PartNo", stockItem.partNo)
//            put("Rev", stockItem.rev)
//            put("Lot", stockItem.lot)
//            put("Qty", stockItem.qty)
//            put("ProductionDate", stockItem.productionDate)
//            put("CountryOfProduction", stockItem.countryOfProduction)
//            put("SerialNumber", stockItem.serialNumber)
//            put("Date", stockItem.date)
//            put("TimeStamp", stockItem.timeStamp)
//            put("User", stockItem.user)
//            put("ContBolNum", stockItem.contBolNum)
//        }
//
//        val insertedId = db.insert("StockList", null, values)
//        db.close()
//        return insertedId
//    }
//
//    private fun getStockListForLastTraceability(): List<StockList> {
//        val db = dbHelper.readableDatabase
//        val stockList = mutableListOf<StockList>()
//
//        // 🔹 Obtener el último `IDTraceabilityStockList`
//        val lastTraceabilityStock = traceabilityStockListRepository.getLastInserted(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
//        val traceabilityId = lastTraceabilityStock?.id ?: return emptyList() // Si no hay ID, retorna lista vacía
//
//        //val query = "SELECT * FROM StockList WHERE IDTraceabilityStockList = ? ORDER BY ID DESC"
//        val query = "SELECT SL.* FROM StockList SL INNER JOIN TraceabilityStockList TSL ON SL.IDTraceabilityStockList = TSL.ID WHERE SL.IDTraceabilityStockList = ? ORDER BY SL.ID DESC"
//        val cursor = db.rawQuery(query, arrayOf(traceabilityId.toString()))
//
//        cursor.use {
//            while (it.moveToNext()) {
//                stockList.add(cursorToStock(it))
//            }
//        }
//
//        return stockList
//    }
//
//    private fun getStockListForLastTraceabilityFinished(): List<StockList> {
//        val db = dbHelper.readableDatabase
//        val stockList = mutableListOf<StockList>()
//
//        // 🔹 Obtener el último `IDTraceabilityStockList`
//        val lastTraceabilityStock = traceabilityStockListRepository.getLastInsertedFinished(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
//        val traceabilityId = lastTraceabilityStock?.id ?: return emptyList() // Si no hay ID, retorna lista vacía
//
//        //val query = "SELECT * FROM StockList WHERE IDTraceabilityStockList = ? ORDER BY ID DESC"
//        val query = "SELECT SL.* FROM StockList SL INNER JOIN TraceabilityStockList TSL ON SL.IDTraceabilityStockList = TSL.ID WHERE SL.IDTraceabilityStockList = ? ORDER BY SL.ID DESC"
//        val cursor = db.rawQuery(query, arrayOf(traceabilityId.toString()))
//
//        cursor.use {
//            while (it.moveToNext()) {
//                stockList.add(cursorToStock(it))
//            }
//        }
//
//        return stockList
//    }
//
//    private fun convertToStockList(parsedData: BarcodeData): List<StockList> {
//        val destination = movementType.getDestinationInMovementTypesByTypeandUserType(moduleName, sharedPreferences.getString("userType", "Unknown") ?: "Unknown")
//        val source = movementType.getSourceInMovementTypesByTypeandUserType(moduleName, sharedPreferences.getString("userType", "Unknown") ?: "Unknown")
//        val lastTraceability = traceabilityStockListRepository.getLastInserted(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
//        val traceId = lastTraceability?.id ?: 0
//        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//        val currentDate = sdf.format(Date())
//
//        // Función para crear un StockList individual según los parámetros
//        @RequiresApi(Build.VERSION_CODES.O)
//
//        fun buildStock(
//            partNumber: String?,
//            rev: String?,
//            count: Int?,
//            pallet: String?,
//            productionDate: String?,
//            country: String?,
//            serial: String?
//        ): StockList {
//            return StockList(
//                id = 0,
//                idTraceabilityStockList = traceId,
//                company = country ?: "001",  // 🔹 Si es null, usar "001"
//                source = source,//sharedPreferences.getString("userType", "Unknown") ?: "Unknown",//lastTraceability?.source ?: "Unknown",
//                //sourceLoc = lastTraceability?.sourceLoc ?: "N/A", // 🔹 Evitar valores vacíos
//                sourceLoc = "Unknown Source", // 🔹 Evitar valores vacíos
//                destination = destination,//lastTraceability?.destination ?: "Unknown Destination",
//                //destinationLoc = lastTraceability?.destinationLoc ?: "N/A",
//                destinationLoc = "Unknown Destination",
//                pallet = pallet ?: "N/A",
//                partNo = partNumber ?: "Unknown",
//                rev = rev ?: "N/A",
//                lot = lastTraceability?.batchNumber ?: "N/A",
//                qty = count ?: 1,
//                productionDate = productionDate ?: "N/A",
//                countryOfProduction = country ?: "Unknown",
//                serialNumber = serial ?: "N/A",
//                date = currentDate.toString(),
//                timeStamp = currentDate.toString(),
//                user = sharedPreferences.getString("userName", "Unknown") ?: "Unknown",
//                contBolNum = "${lastTraceability?.batchNumber ?: "N/A"} "
//            )
//        }
//
//        return when {
//            // Caso 1: Dos calentadores (pallet + partNumberWH2 != null)
//            parsedData.pallet != null && parsedData.partNumberWH2 != null -> {
//                val item1 = buildStock(
//                    parsedData.partNumberWH1,
//                    parsedData.revWH1,
//                    parsedData.countOfTradeItemsWH1,
//                    parsedData.pallet,
//                    parsedData.productionDateWH1,
//                    parsedData.countryOfProductionWH1,
//                    parsedData.serialNumberWH1
//                )
//                val item2 = buildStock(
//                    parsedData.partNumberWH2,
//                    parsedData.revWH2,
//                    parsedData.countOfTradeItemsWH2,
//                    parsedData.pallet,
//                    parsedData.productionDateWH2,
//                    parsedData.countryOfProductionWH2,
//                    parsedData.serialNumberWH2
//                )
//                listOf(item1, item2)
//            }
//
//            // Caso 2: un solo calentador sin pallet (pallet == null), usando “partNumber” normal
//            parsedData.pallet == null && parsedData.partNumber != null -> {
//                val singleItem = buildStock(
//                    parsedData.partNumber,
//                    parsedData.rev,
//                    parsedData.countOfTradeItems,
//                    null,
//                    parsedData.productionDate,
//                    parsedData.countryOfProduction,
//                    parsedData.serialNumber
//                )
//                listOf(singleItem)
//            }
//
//            // Caso 3: un solo calentador con pallet pero sin partNumberWH2 (ej. un “WH1”)
//            parsedData.pallet != null && parsedData.partNumberWH2 == null -> {
//                val singleItem = buildStock(
//                    parsedData.partNumberWH1,
//                    parsedData.revWH1,
//                    parsedData.countOfTradeItemsWH1,
//                    parsedData.pallet,
//                    parsedData.productionDateWH1,
//                    parsedData.countryOfProductionWH1,
//                    parsedData.serialNumberWH1
//                )
//                listOf(singleItem)
//            }
//
//            // Caso 4: otra variante o error
//            else -> emptyList() // Devuelve lista vacía si no hay datos
//        }
//    }
//
//    // Función para enviar el correo con la información del último lote
//    private fun sendLastBatchEmail() {
//        // Obtener la información del último lote (la cabecera y los items)
//        val lastTraceability = traceabilityStockListRepository.getLastInsertedFinished(moduleName,sharedPreferences.getString("userName", "Unknown") ?: "Unknown") ?: return
//        //val stockItems = getStockListForLastTraceability()
//        val stockItems = getStockListForLastTraceabilityFinished()
//        if (stockItems.isEmpty()) return
//
//        // Construir el contenido del archivo TXT
//        //val header = "ID;IDTraceabilityStockList;Company;Source;SourceLoc;Destination;DestinationLoc;Pallet;PartNo;Rev;Lot;Qty;ProductionDate;CountryOfProduction;SerialNumber;Date;TimeStamp;User;ContBolNum"
//        val rows = stockItems.map { stock ->
//            val productionDateFormatted = stock.productionDate?.let { dateStr ->
//                if (dateStr.length == 6) "20$dateStr" else dateStr
//            } ?: ""
//
//            listOf(
//                //stock.id.toString(),
//                "150",
//                //stock.idTraceabilityStockList.toString(),
//                //stock.company,
//                stock.source.trim(),
//                "",
//                //stock.sourceLoc ?: "",
//                stock.destination.trim(),
//                "",
//                //stock.destinationLoc ?: "",
//                //stock.pallet ?: "",
//                stock.partNo.trim(),
//                stock.rev.trim(),
//                stock.serialNumber.toString(),
//                stock.qty.toString().trim(),
//                productionDateFormatted,
//                //stock.productionDate ?: "",
//                //stock.countryOfProduction ?: "",
//                //stock.serialNumber ?: "",
//                //stock.date,
//                //stock.timeStamp,
//                stock.user.trim(),
//                "${stock.contBolNum.trim()}-${stock.pallet ?: ""}",
//                ""
//            ).joinToString(";")
//        }
//        //val fileContent = "$header\n${rows.joinToString("\n")}"
//        val fileContent = rows.joinToString("\n")
//
//        // Obtener el correo destinatario usando IEmailRepository
//        val emailRepository = EmailRepository(MyDatabaseHelper(requireContext()))
//        val recipientEmail = emailRepository.getEmail()?.email ?: return
//
//        // Obtener la configuración del remitente usando IEmailSenderRepository
//        val dbHelper = MyDatabaseHelper(requireContext())
//        val emailSenderRepository = EmailSenderRepository(dbHelper)
//        val senderData = emailSenderRepository.getEmailSender() ?: return
//        val emailSenderService = EmailSenderService(senderData.email, senderData.password)
//
//        // Enviar el correo con adjunto (ejecutando en un hilo secundario)
//        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                emailSenderService.sendEmailWithAttachment(
//                    to = recipientEmail,
//                    subject = "Lote finalizado: ${lastTraceability.batchNumber}",
//                    body = "Adjunto se envía la información del último lote.",
//                    attachmentName = "lote_${lastTraceability.batchNumber}.txt",
//                    attachmentContent = fileContent
//                )
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(requireContext(), "Correo enviado con la información del lote", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(requireContext(), "Error al enviar correo: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//
//    private fun updateCounterUI() {
//        val lastTraceability = traceabilityStockListRepository.getLastInserted(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
//
//        if (lastTraceability == null) {
//            // Si no existe ningún registro, mostramos 0/0
//            binding.textViewCounter.text = "2Calentadores: 0 / 0"
//            return
//        }
//
//        if (lastTraceability.finish) {
//            // Si el último lote ya está finalizado, también mostramos 0/0
//            binding.textViewCounter.text = "Calentadores: 0 / 0"
//            return
//        }
//
//        // Si el lote no está finalizado, calculamos cuántos lleva
//        val scannedCount = getStockListForLastTraceability().size
//        val totalHeaters = lastTraceability.numberOfHeaters
//        binding.textViewCounter.text = "Calentadores: $scannedCount / $totalHeaters"
//    }
//
//
//
//    private fun setupRecyclerView() {
//        adapter = ReceivingAdapter(stockList) { itemToDelete ->
//            removeStockItem(itemToDelete)  // <- Aquí definimos la acción de borrar
//        }
//        binding.recyclerViewStockList.layoutManager = LinearLayoutManager(requireContext())
//        binding.recyclerViewStockList.adapter = adapter
//    }
//
//
//    private fun removeStockItem(item: StockList) {
//        // Eliminar de la base de datos
//        val rowsDeleted = dbHelper.writableDatabase.delete(
//            "StockList",
//            "ID = ?",
//            arrayOf(item.id.toString())
//        )
//
//        if (rowsDeleted > 0) {
//            // Remover el ítem de la lista en memoria y notificar al adapter
//            stockList.remove(item)
//            adapter.notifyDataSetChanged()
//
//            // Actualizar contador
//            updateCounterUI()
//
//            // Actualizar el registro de trazabilidad según la nueva cantidad de piezas escaneadas
//            val lastTraceability = traceabilityStockListRepository.getLastInserted(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
//            if (lastTraceability != null) {
//                val scannedItems = getStockListForLastTraceability()
//                val scannedCount = scannedItems.size
//
//                // Si se elimina una pieza y el total es menor que el esperado, se desmarca la finalización.
//                val updatedTraceability = if (scannedCount < lastTraceability.numberOfHeaters) {
//                    lastTraceability.copy(
//                        finish = false,
//                        numberOfHeatersFinished = scannedCount
//                    )
//                } else {
//                    // Si aún se cumple o se supera, actualizamos solo el contador.
//                    lastTraceability.copy(numberOfHeatersFinished = scannedCount)
//                }
//                traceabilityStockListRepository.update(updatedTraceability)
//            }
//        } else {
//            // Mostrar un mensaje de error si no se pudo borrar
//            Toast.makeText(requireContext(), "Error al borrar el ítem", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun finalizeRecordAndSendEmail() {
//        val userName = sharedPreferences.getString("userName", "Unknown") ?: "Unknown"
//        val currentRecord = traceabilityStockListRepository.getLastInserted(moduleName, userName)
//        if (currentRecord != null && !currentRecord.finish) {
//            // Marcar el registro actual como finalizado
//            val updatedRecord = currentRecord.copy(finish = true)
//            traceabilityStockListRepository.update(updatedRecord)
//            // Enviar correo con el lote finalizado
//            sendLastBatchEmail()
//            Toast.makeText(requireContext(), "Registro finalizado y correo enviado", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(requireContext(), "No hay registro activo para finalizar.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun cursorToStock(cursor: Cursor): StockList {
//        return StockList(
//            id = cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
//            idTraceabilityStockList = cursor.getInt(cursor.getColumnIndexOrThrow("IDTraceabilityStockList")),
//            company = cursor.getString(cursor.getColumnIndexOrThrow("Company")),
//            source = cursor.getString(cursor.getColumnIndexOrThrow("Source")),
//            sourceLoc = cursor.getColumnIndex("SourceLoc").takeIf { it != -1 }?.let { cursor.getString(it) },
//            destination = cursor.getString(cursor.getColumnIndexOrThrow("Destination")),
//            destinationLoc = cursor.getColumnIndex("DestinationLoc").takeIf { it != -1 }?.let { cursor.getString(it) },
//            pallet = cursor.getColumnIndex("Pallet").takeIf { it != -1 }?.let { cursor.getString(it) },
//            partNo = cursor.getString(cursor.getColumnIndexOrThrow("PartNo")),
//            rev = cursor.getString(cursor.getColumnIndexOrThrow("Rev")),
//            lot = cursor.getString(cursor.getColumnIndexOrThrow("Lot")),
//            qty = cursor.getInt(cursor.getColumnIndexOrThrow("Qty")),
//            productionDate = cursor.getColumnIndex("ProductionDate").takeIf { it != -1 }?.let { cursor.getString(it) },
//            countryOfProduction = cursor.getColumnIndex("CountryOfProduction").takeIf { it != -1 }?.let { cursor.getString(it) },
//            serialNumber = cursor.getColumnIndex("SerialNumber").takeIf { it != -1 }?.let { cursor.getString(it) },
//            date = cursor.getString(cursor.getColumnIndexOrThrow("Date")),
//            timeStamp = cursor.getString(cursor.getColumnIndexOrThrow("TimeStamp")),
//            user = cursor.getString(cursor.getColumnIndexOrThrow("User")),
//            contBolNum = cursor.getString(cursor.getColumnIndexOrThrow("ContBolNum"))
//        )
//    }
//}
package com.essency.essencystockmovement.data.UI.Home.ui.inventory

import android.annotation.SuppressLint
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
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.UI.Home.ui.receiving.ReceivingAdapter
import com.essency.essencystockmovement.data.UtilClass.BarcodeParser
import com.essency.essencystockmovement.data.UtilClass.EmailSenderService
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.BarcodeData
import com.essency.essencystockmovement.data.model.StockList
import com.essency.essencystockmovement.data.model.TraceabilityStockList
import com.essency.essencystockmovement.data.repository.EmailRepository
import com.essency.essencystockmovement.data.repository.EmailSenderRepository
import com.essency.essencystockmovement.data.repository.MovementTypeRepository
import com.essency.essencystockmovement.data.repository.TraceabilityStockListRepository
import com.essency.essencystockmovement.databinding.FragmentStockListAuditBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class InventoryFragment : BaseFragment() {

    private var _binding: FragmentStockListAuditBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReceivingAdapter
    private val stockList = mutableListOf<StockList>()
    private lateinit var barcodeParser: BarcodeParser
    private lateinit var traceabilityStockListRepository: TraceabilityStockListRepository
    private lateinit var movementType: MovementTypeRepository
    private lateinit var sharedPreferences: SharedPreferences
    private var moduleName = "INVENTARIO"
    private lateinit var dbHelper: MyDatabaseHelper

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        barcodeParser = BarcodeParser()
        _binding = FragmentStockListAuditBinding.inflate(inflater, container, false)
        dbHelper = MyDatabaseHelper(requireContext())
        traceabilityStockListRepository = TraceabilityStockListRepository(dbHelper)
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        movementType = MovementTypeRepository(dbHelper)

        setupRecyclerView()
        stockList.addAll(getStockListForLastTraceability())
        adapter.notifyDataSetChanged()
        updateCounterUI()

        binding.editTextNewStockItem.setBackgroundColor(Color.WHITE)
        binding.editTextNewStockItem.requestFocus()
        setupTextInputValidation()

        // Listener para finalizar registro usando el botón de correo
        binding.btnMail.setOnClickListener {
            finalizeRecordAndSendEmail()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.editTextNewStockItem.requestFocus()
    }

    private fun setupTextInputValidation() {
        binding.editTextNewStockItem.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {

                val userName = sharedPreferences.getString("userName", "Unknown") ?: "Unknown"
                var currentRecord = traceabilityStockListRepository.getLastInserted(moduleName, userName)
                // Si no hay registro activo o el anterior ya está finalizado, se crea uno nuevo sin límite.
                if (currentRecord == null || currentRecord.finish) {
                    val timeStamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    else "00:00:00"
                    val source = movementType.getSourceInMovementTypesByTypeandUserType(moduleName,
                        sharedPreferences.getString("userType", "Unknown") ?: "Unknown")
                    val destination = movementType.getDestinationInMovementTypesByTypeandUserType(moduleName,
                        sharedPreferences.getString("userType", "Unknown") ?: "Unknown")
                    val newRecord = TraceabilityStockList(
                        batchNumber = "",
                        movementType = moduleName,
                        numberOfHeaters = 0, // Sin límite
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
                    traceabilityStockListRepository.insert(newRecord, moduleName, userName)
                    currentRecord = traceabilityStockListRepository.getLastInserted(moduleName, userName)
                }

                // Procesa el input del código
                val input = binding.editTextNewStockItem.text.toString().trim()
                if (input.isNotEmpty()) {
                    val parsedData = barcodeParser.parseBarcode(input)
                    if (parsedData != null) {
                        val scannedSerial = parsedData.serialNumberWH1
                            ?: parsedData.serialNumberWH2
                            ?: parsedData.serialNumber
                        if (stockList.any { it.serialNumber == scannedSerial }) {
                            binding.editTextNewStockItem.error = "Este dato ya fue escaneado"
                            return@OnEditorActionListener true
                        }
                        val stockItems = convertToStockList(parsedData)
                        for (item in stockItems) {
                            val insertedId = insertNewStockItem(item)
                            if (insertedId != -1L) {
                                val itemWithId = item.copy(id = insertedId.toInt())
                                stockList.add(itemWithId)
                                adapter.notifyItemInserted(stockList.size - 1)
                            } else {
                                binding.editTextNewStockItem.error = "Error al insertar ítem"
                            }
                        }
                        adapter.notifyDataSetChanged()
                        updateCounterUI()
                        binding.editTextNewStockItem.text.clear()
                    } else {
                        binding.editTextNewStockItem.error = "Formato de código inválido"
                    }
                }
                return@OnEditorActionListener true
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
    }

    private fun getStockListForLastTraceability(): List<StockList> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<StockList>()
        val lastRecord = traceabilityStockListRepository.getLastInserted(moduleName,
            sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
        val traceabilityId = lastRecord?.id ?: return emptyList()
        val query = "SELECT SL.* FROM StockList SL INNER JOIN TraceabilityStockList TSL ON SL.IDTraceabilityStockList = TSL.ID WHERE SL.IDTraceabilityStockList = ? ORDER BY SL.ID DESC"
        val cursor = db.rawQuery(query, arrayOf(traceabilityId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToStock(it))
            }
        }
        return list
    }

    private fun getStockListForLastTraceabilityFinished(): List<StockList> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<StockList>()
        val lastRecord = traceabilityStockListRepository.getLastInsertedFinished(moduleName,
            sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
        val traceabilityId = lastRecord?.id ?: return emptyList()
        val query = "SELECT SL.* FROM StockList SL INNER JOIN TraceabilityStockList TSL ON SL.IDTraceabilityStockList = TSL.ID WHERE SL.IDTraceabilityStockList = ? ORDER BY SL.ID DESC"
        val cursor = db.rawQuery(query, arrayOf(traceabilityId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToStock(it))
            }
        }
        return list
    }

    private fun convertToStockList(parsedData: BarcodeData): List<StockList> {
        val destination = movementType.getDestinationInMovementTypesByTypeandUserType(moduleName,
            sharedPreferences.getString("userType", "Unknown") ?: "Unknown")
        val source = movementType.getSourceInMovementTypesByTypeandUserType(moduleName,
            sharedPreferences.getString("userType", "Unknown") ?: "Unknown")
        val lastRecord = traceabilityStockListRepository.getLastInserted(moduleName,
            sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
        val traceId = lastRecord?.id ?: 0
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val currentDate = sdf.format(Date())
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
                company = country ?: "001",
                source = source,
                sourceLoc = "Unknown Source",
                destination = destination,
                destinationLoc = "Unknown Destination",
                pallet = pallet ?: "N/A",
                partNo = partNumber ?: "Unknown",
                rev = rev ?: "N/A",
                lot = lastRecord?.batchNumber ?: "N/A",
                qty = count ?: 1,
                productionDate = productionDate ?: "N/A",
                countryOfProduction = country ?: "Unknown",
                serialNumber = serial ?: "N/A",
                date = currentDate,
                timeStamp = currentDate,
                user = sharedPreferences.getString("userName", "Unknown") ?: "Unknown",
                contBolNum = "${lastRecord?.batchNumber ?: "N/A"} "
            )
        }
        return when {
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
            else -> emptyList()
        }
    }

    private fun sendLastBatchEmail() {
        val lastRecord = traceabilityStockListRepository.getLastInsertedFinished(moduleName,
            sharedPreferences.getString("userName", "Unknown") ?: "Unknown") ?: return
        val stockItems = getStockListForLastTraceabilityFinished()
        if (stockItems.isEmpty()) return
        val rows = stockItems.map { stock ->
            val productionDateFormatted = stock.productionDate?.let { dateStr ->
                if (dateStr.length == 6) "20$dateStr" else dateStr
            } ?: ""
            listOf(
                "Q",
                "${stock.partNo.trim()} ${stock.rev.trim()}",
                "PRO",
                "${stock.contBolNum.trim()}-${stock.pallet ?: ""}",
                "${stock.qty}"
            ).joinToString(";")
        }
        val fileContent = rows.joinToString("\n")
        val emailRepository = EmailRepository(MyDatabaseHelper(requireContext()))
        val recipientEmail = emailRepository.getEmail()?.email ?: return
        val dbHelper = MyDatabaseHelper(requireContext())
        val emailSenderRepository = EmailSenderRepository(dbHelper)
        val senderData = emailSenderRepository.getEmailSender() ?: return
        val emailSenderService = EmailSenderService(senderData.email, senderData.password)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                emailSenderService.sendEmailWithAttachment(
                    to = recipientEmail,
                    subject = "Lote finalizado: ${lastRecord.batchNumber}",
                    body = "Adjunto se envía la información del último lote.",
                    attachmentName = "lote_${lastRecord.batchNumber}.txt",
                    attachmentContent = fileContent
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Correo enviado con la información del lote", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al enviar correo: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateCounterUI() {
        val lastRecord = traceabilityStockListRepository.getLastInserted(moduleName,
            sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
        if (lastRecord == null || lastRecord.finish) {
            binding.textViewCounter.text = "Calentadores: 0 / 0"
            return
        }
        val scannedCount = getStockListForLastTraceability().size
        val totalHeaters = lastRecord.numberOfHeaters
        binding.textViewCounter.text = "Calentadores: $scannedCount / $totalHeaters"
    }

    private fun setupRecyclerView() {
        adapter = ReceivingAdapter(stockList) { itemToDelete ->
            removeStockItem(itemToDelete)
        }
        binding.recyclerViewStockList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStockList.adapter = adapter
    }

    private fun removeStockItem(item: StockList) {
        val rowsDeleted = dbHelper.writableDatabase.delete(
            "StockList",
            "ID = ?",
            arrayOf(item.id.toString())
        )
        if (rowsDeleted > 0) {
            stockList.remove(item)
            adapter.notifyDataSetChanged()
            updateCounterUI()
            val lastRecord = traceabilityStockListRepository.getLastInserted(moduleName,
                sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
            if (lastRecord != null) {
                val scannedItems = getStockListForLastTraceability()
                val scannedCount = scannedItems.size
                val updatedRecord = if (scannedCount < lastRecord.numberOfHeaters)
                    lastRecord.copy(finish = false, numberOfHeatersFinished = scannedCount)
                else
                    lastRecord.copy(numberOfHeatersFinished = scannedCount)
                traceabilityStockListRepository.update(updatedRecord)
            }
        } else {
            Toast.makeText(requireContext(), "Error al borrar el ítem", Toast.LENGTH_SHORT).show()
        }
    }

private fun finalizeRecordAndSendEmail() {
    val userName = sharedPreferences.getString("userName", "Unknown") ?: "Unknown"
    val currentRecord = traceabilityStockListRepository.getLastInserted(moduleName, userName)
    if (currentRecord != null && !currentRecord.finish) {
        // Obtener cantidad de calentadores escaneados
        val scannedCount = getStockListForLastTraceability().size

        // Actualizar el registro: marcarlo como finalizado, actualizar NumberOfHeaters, NumberOfHeatersFinished y SendByEmail a true (1)
        val updatedRecord = currentRecord.copy(
            finish = true,
            numberOfHeaters = scannedCount,        // Actualiza con el total escaneado
            numberOfHeatersFinished = scannedCount,  // Actualiza con el total escaneado
            sendByEmail = true                       // Para indicar que ya se envió el correo
        )
        traceabilityStockListRepository.update(updatedRecord)
        sendLastBatchEmail()

        // Limpiar la lista y actualizar la UI
        stockList.clear()
        adapter.notifyDataSetChanged()
        updateCounterUI()
        Toast.makeText(requireContext(), "Registro finalizado, correo enviado y lista limpia", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(requireContext(), "No hay registro activo para finalizar.", Toast.LENGTH_SHORT).show()
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
