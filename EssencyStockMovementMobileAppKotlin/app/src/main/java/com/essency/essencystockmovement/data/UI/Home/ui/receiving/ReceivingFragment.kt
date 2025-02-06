package com.essency.essencystockmovement.data.UI.Home.ui.receiving

import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.StockList
import com.essency.essencystockmovement.databinding.FragmentStockListBinding

class ReceivingFragment : BaseFragment() {

    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReceivingAdapter
    private val stockList = mutableListOf<StockList>()

    private lateinit var dbHelper: MyDatabaseHelper
    private var palletRegex: Regex? = null // Expresión regular cargada desde la base de datos

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockListBinding.inflate(inflater, container, false)
        dbHelper = MyDatabaseHelper(requireContext())

        setupRecyclerView()
        loadPalletRegex() // Cargar la regex de la DB
        setupTextInputValidation() // Validar input con la regex

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ReceivingAdapter(stockList) { itemToDelete ->
            removeStockItem(itemToDelete)
        }
        binding.recyclerViewStockList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStockList.adapter = adapter
    }

    private fun setupTextInputValidation() {
        binding.editTextNewStockItem.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()

                if (palletRegex != null) {
                    if (!palletRegex!!.matches(input)) {
                        binding.editTextNewStockItem.error = "Invalid format!"
                    } else {
                        binding.editTextNewStockItem.error = null // Elimina el error si es válido
                    }
                }
            }
        })
    }

    private fun loadPalletRegex() {
        val db = dbHelper.readableDatabase
        val query = "SELECT RegularExpression FROM AppConfigurationRegularExpression WHERE NameRegularExpression = 'Pallet'"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val regexString = cursor.getString(0)
            palletRegex = regexString.toRegex()
        }

        cursor.close()
        db.close()
    }

    private fun removeStockItem(stockItem: StockList) {
        stockList.remove(stockItem)
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
