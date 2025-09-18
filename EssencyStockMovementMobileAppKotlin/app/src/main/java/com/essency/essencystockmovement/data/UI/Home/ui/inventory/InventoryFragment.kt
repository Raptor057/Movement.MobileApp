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
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.essency.essencystockmovement.R
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
    private lateinit var repository: TraceabilityStockListRepository
    private lateinit var movementType: MovementTypeRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: MyDatabaseHelper

    private val moduleName = "INVENTARIO"

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        barcodeParser = BarcodeParser()
        _binding = FragmentStockListAuditBinding.inflate(inflater, container, false)

        dbHelper = MyDatabaseHelper(requireContext())
        repository = TraceabilityStockListRepository(dbHelper)
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        movementType = MovementTypeRepository(dbHelper)

        // Recycler
        setupRecyclerView()
        stockList.addAll(getStockListForLastTraceability())
        adapter.notifyDataSetChanged()

        // Contador inicial
        updateCounterUI()

        // UX input
        binding.editTextNewStockItem.setBackgroundColor(Color.WHITE)
        binding.editTextNewStockItem.setShowSoftInputOnFocus(false)
        binding.editTextNewStockItem.requestFocus()

        // Botón enviar (manual)
        binding.buttonSendEmailNow.setOnClickListener {
            sendEmailFromCurrentListIfAny()
        }

        // Validación de entrada/scan
        setupTextInputValidation()

        // Asegura foco y oculta teclado suave
        binding.editTextNewStockItem.post {
            val len = binding.editTextNewStockItem.text?.length ?: 0
            binding.editTextNewStockItem.setSelection(len)
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editTextNewStockItem.windowToken, 0)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.editTextNewStockItem.post {
            val len = binding.editTextNewStockItem.text?.length ?: 0
            binding.editTextNewStockItem.setSelection(len)
            binding.editTextNewStockItem.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editTextNewStockItem.windowToken, 0)
        }
    }

    private fun setupTextInputValidation() {
        binding.editTextNewStockItem.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {

                val userName = sharedPreferences.getString("userName", "Unknown") ?: "Unknown"

                // Asegura que exista un registro de trazabilidad abierto (inventario no tiene límite)
                var currentRecord = repository.getLastInserted(moduleName, userName)
                if (currentRecord == null || currentRecord.finish) {
                    val timeStamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) else "00:00:00"
                    val source = movementType.getSourceInMovementTypesByTypeandUserType(
                        moduleName, sharedPreferences.getString("userType", "Unknown") ?: "Unknown"
                    )
                    val destination = movementType.getDestinationInMovementTypesByTypeandUserType(
                        moduleName, sharedPreferences.getString("userType", "Unknown") ?: "Unknown"
                    )
                    repository.insert(
                        TraceabilityStockList(
                            id = 0,
                            batchNumber = "",
                            movementType = moduleName,
                            numberOfHeaters = 0,                 // sin meta
                            numberOfHeatersFinished = 0,
                            finish = false,
                            sendByEmail = false,
                            createdBy = userName,
                            timeStamp = timeStamp,
                            notes = "",
                            source = source,
                            destination = destination
                        ), moduleName, userName
                    )
                    currentRecord = repository.getLastInserted(moduleName, userName)
                }

                val input = binding.editTextNewStockItem.text.toString().trim()
                if (input.isNotEmpty()) {
                    val parsedData = barcodeParser.parseBarcode(input)
                    if (parsedData != null) {
                        val scannedSerial = parsedData.serialNumberWH1
                            ?: parsedData.serialNumberWH2
                            ?: parsedData.serialNumber

                        // Duplicados
                        if (stockList.any { it.serialNumber == scannedSerial }) {
                            binding.editTextNewStockItem.error = getString(R.string.error_duplicate_barcode)
                            return@OnEditorActionListener true
                        }

                        val items = convertToStockList(parsedData)
                        for (item in items) {
                            val insertedId = insertNewStockItem(item)
                            if (insertedId != -1L) {
                                stockList.add(item.copy(id = insertedId.toInt()))
                                adapter.notifyItemInserted(stockList.size - 1)
                            } else {
                                binding.editTextNewStockItem.error = getString(R.string.error_inserting_item)
                            }
                        }
                        adapter.notifyDataSetChanged()
                        updateCounterUI()

                        // Limpia input y mantiene foco
                        binding.editTextNewStockItem.text?.clear()
                        binding.editTextNewStockItem.post {
                            binding.editTextNewStockItem.requestFocus()
                        }

                        // Inventario NO envía ni finaliza automáticamente
                        // Solo actualizamos numberOfHeatersFinished con lo escaneado
                        val scanned = getStockListForLastTraceability().size
                        repository.getLastInserted(moduleName, userName)?.let { last ->
                            repository.update(last.copy(numberOfHeatersFinished = scanned))
                        }

                        // Mensaje de listo para enviar cuando hay por lo menos 1
                        if (stockList.isNotEmpty()) {
                            Toast.makeText(requireContext(), getString(R.string.toast_batch_ready_to_send), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.editTextNewStockItem.error = getString(R.string.error_invalid_barcode_format)
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun sendEmailFromCurrentListIfAny() {
        val userName = sharedPreferences.getString("userName", "Unknown") ?: "Unknown"
        val last = repository.getLastInserted(moduleName, userName)

        if (last == null) {
            Toast.makeText(requireContext(), getString(R.string.toast_missing_batch_info), Toast.LENGTH_SHORT).show()
            return
        }

        val scannedCount = getStockListForLastTraceability().size
        if (scannedCount <= 0) {
            Toast.makeText(requireContext(), getString(R.string.email_incomplete_batch, 0, 0), Toast.LENGTH_SHORT).show()
            return
        }

        // Marcar terminado SOLO aquí
        repository.update(last.copy(
            finish = true,
            numberOfHeaters = scannedCount,
            numberOfHeatersFinished = scannedCount,
            sendByEmail = true
        ))

        // Enviar correo (formato de inventario que ya tenías)
        sendLastBatchEmail()

        // Limpiar UI
        stockList.clear()
        adapter.notifyDataSetChanged()
        binding.editTextNewStockItem.text?.clear()
        updateCounterUI()
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
        val last = repository.getLastInserted(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
        val traceabilityId = last?.id ?: return emptyList()
        val query = "SELECT SL.* FROM StockList SL INNER JOIN TraceabilityStockList TSL ON SL.IDTraceabilityStockList = TSL.ID WHERE SL.IDTraceabilityStockList = ? ORDER BY SL.ID DESC"
        val cursor = db.rawQuery(query, arrayOf(traceabilityId.toString()))
        cursor.use { while (it.moveToNext()) list.add(cursorToStock(it)) }
        return list
    }

    private fun getStockListForLastTraceabilityFinished(): List<StockList> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<StockList>()
        val last = repository.getLastInsertedFinished(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
        val traceabilityId = last?.id ?: return emptyList()
        val query = "SELECT SL.* FROM StockList SL INNER JOIN TraceabilityStockList TSL ON SL.IDTraceabilityStockList = TSL.ID WHERE SL.IDTraceabilityStockList = ? ORDER BY SL.ID DESC"
        val cursor = db.rawQuery(query, arrayOf(traceabilityId.toString()))
        cursor.use { while (it.moveToNext()) list.add(cursorToStock(it)) }
        return list
    }

    private fun convertToStockList(parsedData: BarcodeData): List<StockList> {
        val destination = movementType.getDestinationInMovementTypesByTypeandUserType(
            moduleName, sharedPreferences.getString("userType", "Unknown") ?: "Unknown"
        )
        val source = movementType.getSourceInMovementTypesByTypeandUserType(
            moduleName, sharedPreferences.getString("userType", "Unknown") ?: "Unknown"
        )
        val last = repository.getLastInserted(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
        val traceId = last?.id ?: 0
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
        ) = StockList(
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
            lot = last?.batchNumber ?: "N/A",
            qty = count ?: 1,
            productionDate = productionDate ?: "N/A",
            countryOfProduction = country ?: "Unknown",
            serialNumber = serial ?: "N/A",
            date = currentDate,
            timeStamp = currentDate,
            user = sharedPreferences.getString("userName", "Unknown") ?: "Unknown",
            contBolNum = "${last?.batchNumber ?: "N/A"} "
        )

        return when {
            parsedData.pallet != null && parsedData.partNumberWH2 != null -> listOf(
                buildStock(parsedData.partNumberWH1, parsedData.revWH1, parsedData.countOfTradeItemsWH1,
                    parsedData.pallet, parsedData.productionDateWH1, parsedData.countryOfProductionWH1, parsedData.serialNumberWH1),
                buildStock(parsedData.partNumberWH2, parsedData.revWH2, parsedData.countOfTradeItemsWH2,
                    parsedData.pallet, parsedData.productionDateWH2, parsedData.countryOfProductionWH2, parsedData.serialNumberWH2)
            )
            parsedData.pallet == null && parsedData.partNumber != null -> listOf(
                buildStock(parsedData.partNumber, parsedData.rev, parsedData.countOfTradeItems,
                    null, parsedData.productionDate, parsedData.countryOfProduction, parsedData.serialNumber)
            )
            parsedData.pallet != null && parsedData.partNumberWH2 == null -> listOf(
                buildStock(parsedData.partNumberWH1, parsedData.revWH1, parsedData.countOfTradeItemsWH1,
                    parsedData.pallet, parsedData.productionDateWH1, parsedData.countryOfProductionWH1, parsedData.serialNumberWH1)
            )
            else -> emptyList()
        }
    }

    private fun sendLastBatchEmail() {
        val last = repository.getLastInsertedFinished(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown") ?: return
        val stockItems = getStockListForLastTraceabilityFinished()
        if (stockItems.isEmpty()) return

        // Formato INVENTARIO (el que ya usabas)
        val rows = stockItems.map { stock ->
            listOf(
                "Q",
                "${stock.partNo.trim()} ${stock.rev.trim()}",
                "DILIGE",
                stock.serialNumber.toString(),
                "${stock.qty}"
            ).joinToString(";")
        }
        val fileContent = rows.joinToString("\n")

        val recipientEmail = EmailRepository(MyDatabaseHelper(requireContext())).getEmail()?.email ?: return
        val senderData = EmailSenderRepository(MyDatabaseHelper(requireContext())).getEmailSender() ?: return
        val emailSenderService = EmailSenderService(senderData.email, senderData.password)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                emailSenderService.sendEmailWithAttachment(
                    to = recipientEmail,
                    subject = "Lote finalizado: ${last.batchNumber}",
                    body = "Adjunto se envía la información del último lote.",
                    attachmentName = "lote_${last.batchNumber}.txt",
                    attachmentContent = fileContent
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), getString(R.string.email_sent_successfully), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), getString(R.string.email_send_error, e.message ?: "Unknown"), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateCounterUI() {
        val last = repository.getLastInserted(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
        val scanned = if (last == null) 0 else getStockListForLastTraceability().size
        val total   = last?.numberOfHeaters ?: 0 // en inventario suele ser 0 (sin meta)

        // Reusa tu string tipo "Calentadores: %1$d / %2$d"
        binding.textViewCounter.text = getString(R.string.heater_count, scanned, total)

        // ✅ En inventario habilitamos el botón si hay algo que enviar
        binding.buttonSendEmailNow.isEnabled = (last != null && scanned > 0)
    }

    private fun setupRecyclerView() {
        adapter = ReceivingAdapter(stockList) { itemToDelete -> removeStockItem(itemToDelete) }
        binding.recyclerViewStockList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStockList.adapter = adapter
        binding.recyclerViewStockList.isFocusable = false
        binding.recyclerViewStockList.isFocusableInTouchMode = false
    }

    private fun removeStockItem(item: StockList) {
        val rowsDeleted = dbHelper.writableDatabase.delete("StockList", "ID = ?", arrayOf(item.id.toString()))
        if (rowsDeleted > 0) {
            stockList.remove(item)
            adapter.notifyDataSetChanged()
            updateCounterUI()

            repository.getLastInserted(moduleName, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")?.let { last ->
                val scanned = getStockListForLastTraceability().size
                val updated = if (scanned < last.numberOfHeaters) last.copy(finish = false, numberOfHeatersFinished = scanned)
                else last.copy(numberOfHeatersFinished = scanned)
                repository.update(updated)
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.delete_item_error), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cursorToStock(cursor: Cursor): StockList = StockList(
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
