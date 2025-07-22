package com.essency.essencystockmovement.data.UI.Home.ui.preparingforshipment

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
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.essency.essencystockmovement.data.repository.EmailRepository
import com.essency.essencystockmovement.data.repository.EmailSenderRepository
import com.essency.essencystockmovement.data.repository.MovementTypeRepository
import com.essency.essencystockmovement.data.repository.TraceabilityStockListRepository
import com.essency.essencystockmovement.databinding.FragmentPreparingForShipmentBinding
import com.essency.essencystockmovement.databinding.FragmentStockListBinding
import com.essency.essencystockmovement.databinding.FragmentStockListPreparingForShipmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

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
        movementType = MovementTypeRepository(MyDatabaseHelper(requireContext()))
        binding.editTextNewStockItem.setShowSoftInputOnFocus(false)


        // Inicializa el adapter antes de usar stockList
        setupRecyclerView()

        // Ahora carga los datos en stockList (si es necesario)
        // stockList.addAll(getStockListForLastTraceability())
        adapter.notifyDataSetChanged()

        // Actualiza el contador de entrada (opcional, si lo requieres)
        // updateCounterUI()

        binding.editTextNewStockItem.setBackgroundColor(Color.WHITE)
        binding.editTextNewStockItem.requestFocus()

        // Asegúrate de llamar al método para configurar la validación del input
        setupTextInputValidation()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.editTextNewStockItem.post {
            // colocar el cursor al final (o en la posición que quieras)
            val len = binding.editTextNewStockItem.text?.length ?: 0
            binding.editTextNewStockItem.setSelection(len)

            // volver a pedir foco
            binding.editTextNewStockItem.requestFocus()

            // Opcional: evita que se abra el teclado si solo usas escáner
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editTextNewStockItem.windowToken, 0)
        }
    }


//    override fun onResume() {
//        super.onResume()
//        // Al volver de otra pantalla, recupera el foco
//        binding.editTextNewStockItem.requestFocus()
//    }

    private fun setupTextInputValidation() {
        binding.editTextNewStockItem.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            // Detectar "Enter" desde el teclado físico o botón en pantalla
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {

                // 1) Obtener el registro de trazabilidad actual
                val currentTraceability = repository.getLastInserted(defaultMovementType, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")

                // 2) Verificar si ya hay un lote finalizado o si no existe
                if (currentTraceability == null || currentTraceability.finish) {
                    //Toast.makeText(requireContext(), "El lote actual ya está finalizado. Inicia un nuevo registro.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(requireContext(), getString(R.string.toast_batch_finished), Toast.LENGTH_SHORT).show()
                    return@OnEditorActionListener true
                }

                // 3) Verificar si faltan datos (batchNumber o numberOfHeaters)
                if (currentTraceability.batchNumber.isEmpty() || currentTraceability.numberOfHeaters == 0) {
                    //Toast.makeText(requireContext(), "Por favor, complete los campos en Datos de Recepción.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(requireContext(), getString(R.string.toast_missing_batch_info), Toast.LENGTH_SHORT).show()
                    return@OnEditorActionListener true
                }

                // 4) Ya tenemos un lote abierto. Proceder a parsear el input
                val input = binding.editTextNewStockItem.text.toString().trim()
                if (input.isNotEmpty()) {
                    val parsedData = barcodeParser.parseBarcode(input)

                    if (parsedData != null) {
                        val scannedSerial = parsedData.serialNumberWH1
                            ?: parsedData.serialNumberWH2
                            ?: parsedData.serialNumber

                        // Evitar duplicados
                        val duplicate = stockList.any { it.serialNumber == scannedSerial }
                        if (duplicate) {
                            //binding.editTextNewStockItem.error = "Este dato ya fue escaneado"
                            binding.editTextNewStockItem.error = getString(R.string.error_duplicate_serial)
                            return@OnEditorActionListener true // Evita la inserción
                        }

                        val stockItems = convertToStockList(parsedData)
                        // 1) Ver cuántos calentadores ya llevas
                        val scannedCount = getStockListForLastTraceability().size

                        // 2) Obtener el TraceabilityStockList actual (último insertado)
                        val getlastTraceability = repository.getLastInserted(defaultMovementType, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
                        if (getlastTraceability == null) {
                            // Si no hay lote, no puedes insertar. Regresas con error o como gustes.
                            //binding.editTextNewStockItem.error = "No hay lote activo. Por favor, registra datos de recepción."
                            binding.editTextNewStockItem.error = getString(R.string.error_no_active_batch)

                            return@OnEditorActionListener true
                        }

                        // 3) totalHeaters será un valor Int, pero usamos el ?. para prevenir null y el operador ?: para un default
                        val totalHeaters = getlastTraceability.numberOfHeaters
                        val newItemsCount = stockItems.size

                        // 4) Verificamos si al sumar rebasaríamos el límite
                        if (scannedCount + newItemsCount > totalHeaters) {
//                            binding.editTextNewStockItem.error = "¡Excedes el límite de calentadores! " +
//                                    "Llevas $scannedCount de $totalHeaters, y esta etiqueta añade $newItemsCount."
                            binding.editTextNewStockItem.error = getString(
                                R.string.error_heater_limit_exceeded,
                                scannedCount,
                                totalHeaters,
                                newItemsCount
                            )
                            return@OnEditorActionListener true
                        }

                        // 5) Si no rebasas, ya insertas normalmente
                        for (item in stockItems) {
                            val insertedId = insertNewStockItem(item)
                            if (insertedId != -1L) {
                                // Agregar a la lista y notificar
                                val itemWithId = item.copy(id = insertedId.toInt())
                                stockList.add(itemWithId)
                                adapter.notifyItemInserted(stockList.size - 1)
                            } else {
                                //binding.editTextNewStockItem.error = "Error inserting item"
                                binding.editTextNewStockItem.error = getString(R.string.error_inserting_item)

                            }
                        }
                        adapter.notifyDataSetChanged()
                        updateCounterUI()

                        // Limpiar campo de texto
                        binding.editTextNewStockItem.text?.clear()

                        // Forzar el foco de nuevo
                        binding.editTextNewStockItem.post {
                            binding.editTextNewStockItem.requestFocus()
                        }

                        // Verificar si se completó el lote actual
                        val lastTraceability = repository.getLastInserted(defaultMovementType, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
                        if (lastTraceability != null) {
                            val scannedItems = getStockListForLastTraceability()
                            val scannedCount = scannedItems.size

                            if (scannedCount >= lastTraceability.numberOfHeaters) {
                                // Lote finalizado
                                val updatedTraceability = lastTraceability.copy(finish = true)
                                repository.update(updatedTraceability)
                                //Toast.makeText(requireContext(), "Lote completado. Iniciando nuevo registro.", Toast.LENGTH_SHORT).show()
                                Toast.makeText(requireContext(), getString(R.string.toast_batch_completed), Toast.LENGTH_SHORT).show()

                                // Enviar la información del último lote por correo
                                sendLastBatchEmail()

                                // Limpiar la lista para iniciar un nuevo lote
                                stockList.clear()
                                adapter.notifyDataSetChanged()
                                updateCounterUI()
                            } else {
                                // Aún no se completa
                                val updatedTraceability = lastTraceability.copy(numberOfHeatersFinished = scannedCount)
                                repository.update(updatedTraceability)
                                //Toast.makeText(requireContext(), "Calentador agregado: $scannedCount de ${lastTraceability.numberOfHeaters}", Toast.LENGTH_SHORT).show()
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.toast_heater_added, scannedCount, lastTraceability.numberOfHeaters),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                    } else {
                        //binding.editTextNewStockItem.error = "Invalid barcode format!"
                        binding.editTextNewStockItem.error = getString(R.string.error_invalid_barcode_format)

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
    }

    private fun getStockListForLastTraceability(): List<StockList> {
        val db = dbHelper.readableDatabase
        val stockList = mutableListOf<StockList>()

        // 🔹 Obtener el último `IDTraceabilityStockList`
        val lastTraceabilityStock = repository.getLastInserted(defaultMovementType, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
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

    private fun getStockListForLastTraceabilityFinished(): List<StockList> {
        val db = dbHelper.readableDatabase
        val stockList = mutableListOf<StockList>()

        // 🔹 Obtener el último `IDTraceabilityStockList`
        val lastTraceabilityStock = repository.getLastInsertedFinished(defaultMovementType, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
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

    private fun convertToStockList(parsedData: BarcodeData): List<StockList> {
        val destination = movementType.getDestinationInMovementTypesByTypeandUserType(defaultMovementType, sharedPreferences.getString("userType", "Unknown") ?: "Unknown")
        val source = movementType.getSourceInMovementTypesByTypeandUserType(defaultMovementType, sharedPreferences.getString("userType", "Unknown") ?: "Unknown")
        val lastTraceability = repository.getLastInserted(defaultMovementType, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
        val traceId = lastTraceability?.id ?: 0
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val currentDate = sdf.format(Date())

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
                company = country ?: "001",  // 🔹 Si es null, usar "001"
                source = source,//sharedPreferences.getString("userType", "Unknown") ?: "Unknown",//lastTraceability?.source ?: "Unknown",
                //sourceLoc = lastTraceability?.sourceLoc ?: "N/A", // 🔹 Evitar valores vacíos
                sourceLoc = "Unknown Source", // 🔹 Evitar valores vacíos
                destination = destination,//lastTraceability?.destination ?: "Unknown Destination",
                //destinationLoc = lastTraceability?.destinationLoc ?: "N/A",
                destinationLoc = "Unknown Destination",
                pallet = pallet ?: "N/A",
                partNo = partNumber ?: "Unknown",
                rev = rev ?: "N/A",
                lot = lastTraceability?.batchNumber ?: "N/A",
                qty = count ?: 1,
                productionDate = productionDate ?: "N/A",
                countryOfProduction = country ?: "Unknown",
                serialNumber = serial ?: "N/A",
                date = currentDate.toString(),
                timeStamp = currentDate.toString(),
                user = sharedPreferences.getString("userName", "Unknown") ?: "Unknown",
                contBolNum = "${lastTraceability?.batchNumber ?: "N/A"} "
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

    // Función para enviar el correo con la información del último lote
    private fun sendLastBatchEmail() {
        // Obtener la información del último lote (la cabecera y los items)
        val lastTraceability = repository.getLastInsertedFinished(defaultMovementType,sharedPreferences.getString("userName", "Unknown") ?: "Unknown") ?: return
        //val stockItems = getStockListForLastTraceability()
        val stockItems = getStockListForLastTraceabilityFinished()
        if (stockItems.isEmpty()) return

        // Construir el contenido del archivo TXT
        //val header = "ID;IDTraceabilityStockList;Company;Source;SourceLoc;Destination;DestinationLoc;Pallet;PartNo;Rev;Lot;Qty;ProductionDate;CountryOfProduction;SerialNumber;Date;TimeStamp;User;ContBolNum"
        val rows = stockItems.map { stock ->
            val productionDateFormatted = stock.productionDate?.let { dateStr ->
                if (dateStr.length == 6) "20$dateStr" else dateStr
            } ?: ""

            listOf(
                //stock.id.toString(),
                "150",
                //stock.idTraceabilityStockList.toString(),
                //stock.company,
                stock.source.trim(),
                "",
                //stock.sourceLoc ?: "",
                stock.destination.trim(),
                stock.contBolNum.trim() ?: "",
                //stock.destinationLoc ?: "",
                //stock.pallet ?: "",
                stock.partNo.trim(),
                stock.rev.trim(),
                stock.serialNumber.toString(),
                stock.qty.toString().trim(),
                productionDateFormatted,
                //stock.productionDate ?: "",
                //stock.countryOfProduction ?: "",
                //stock.serialNumber ?: "",
                //stock.date,
                //stock.timeStamp,
                stock.user.trim(),
                "${stock.contBolNum.trim()}-${stock.pallet ?: ""}",
                ""
            ).joinToString(";")
        }
        //val fileContent = "$header\n${rows.joinToString("\n")}"
        val fileContent = rows.joinToString("\n")

        // Obtener el correo destinatario usando IEmailRepository
        val emailRepository = EmailRepository(MyDatabaseHelper(requireContext()))
        val recipientEmail = emailRepository.getEmail()?.email ?: return

        // Obtener la configuración del remitente usando IEmailSenderRepository
        val dbHelper = MyDatabaseHelper(requireContext())
        val emailSenderRepository = EmailSenderRepository(dbHelper)
        val senderData = emailSenderRepository.getEmailSender() ?: return
        val emailSenderService = EmailSenderService(senderData.email, senderData.password)

        // Enviar el correo con adjunto (ejecutando en un hilo secundario)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                emailSenderService.sendEmailWithAttachment(
                    to = recipientEmail,
                    subject = "Lote finalizado: ${lastTraceability.batchNumber}",
                    body = "Adjunto se envía la información del último lote.",
                    attachmentName = "lote_${lastTraceability.batchNumber}.txt",
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
        val lastTraceability = repository.getLastInserted(defaultMovementType, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")

        if (lastTraceability == null) {
            // Si no existe ningún registro, mostramos 0/0
            binding.textViewCounter.text = "2Calentadores: 0 / 0"
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
            val lastTraceability = repository.getLastInserted(defaultMovementType, sharedPreferences.getString("userName", "Unknown") ?: "Unknown")
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