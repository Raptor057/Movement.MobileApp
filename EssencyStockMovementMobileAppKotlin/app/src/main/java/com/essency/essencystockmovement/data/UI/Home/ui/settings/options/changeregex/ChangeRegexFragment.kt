package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changeregex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AppConfigurationRegularExpression
import com.essency.essencystockmovement.data.repository.RegularExpressionRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsRegexBinding

class ChangeRegexFragment : Fragment() {

    private var _binding: FragmentSettingsRegexBinding? = null
    private val binding get() = _binding!!

    private lateinit var regexRepository: RegularExpressionRepository
    private lateinit var regexList: MutableList<AppConfigurationRegularExpression>
    private lateinit var adapter: ArrayAdapter<String>
    private var selectedRegexId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsRegexBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa la base de datos y el repositorio
        val dbHelper = MyDatabaseHelper(requireContext())
        regexRepository = RegularExpressionRepository(dbHelper)

        setupUI()

        return root
    }

    private fun setupUI() {
        // Inicializar lista de expresiones regulares y adaptador
        regexList = regexRepository.getAllRegularExpressions().toMutableList()
        val regexNames = regexList.map { it.nameRegularExpression }

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, regexNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRegexNames.adapter = adapter

        // Configurar el botón de actualización
        binding.buttonUpdateRegex.setOnClickListener {
            val updatedRegex = binding.editTextRegexValue.text.toString()

            if (selectedRegexId == null) {
                Toast.makeText(requireContext(), "Seleccione una expresión regular", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (updatedRegex.isBlank()) {
                Toast.makeText(requireContext(), "El campo de expresión regular no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isUpdated = regexRepository.updateRegularExpression(selectedRegexId!!, updatedRegex)

            if (isUpdated) {
                Toast.makeText(requireContext(), "Expresión regular actualizada correctamente", Toast.LENGTH_SHORT).show()
                reloadRegexList() // Recargar el Spinner y actualizar el texto
            } else {
                Toast.makeText(requireContext(), "Error al actualizar la expresión regular", Toast.LENGTH_SHORT).show()
            }
        }

        // Escuchar la selección del Spinner
        binding.spinnerRegexNames.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRegex = regexList[position]
                selectedRegexId = selectedRegex.id
                binding.editTextRegexValue.setText(selectedRegex.regularExpression)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRegexId = null
                binding.editTextRegexValue.text.clear()
            }
        }
    }

    private fun reloadRegexList() {
        regexList.clear()
        regexList.addAll(regexRepository.getAllRegularExpressions()) // Obtener nueva lista actualizada
        adapter.clear()
        adapter.addAll(regexList.map { it.nameRegularExpression }) // Actualizar los nombres en el Spinner
        adapter.notifyDataSetChanged() // Notificar al adaptador del cambio
        binding.spinnerRegexNames.setSelection(regexList.indexOfFirst { it.id == selectedRegexId })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
