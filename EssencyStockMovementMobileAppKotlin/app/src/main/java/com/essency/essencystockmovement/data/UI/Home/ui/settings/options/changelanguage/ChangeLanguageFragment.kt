package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changelanguage

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.Language
import com.essency.essencystockmovement.data.repository.LanguageRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsLanguageBinding
import java.util.*

class ChangeLanguageFragment : Fragment() {

    private var _binding: FragmentSettingsLanguageBinding? = null
    private val binding get() = _binding!!

    private lateinit var languageRepository: LanguageRepository
    private lateinit var languageList: MutableList<Language>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsLanguageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializar el repositorio de idiomas
        val dbHelper = MyDatabaseHelper(requireContext())
        languageRepository = LanguageRepository(dbHelper)

        setupUI()

        return root
    }

    private fun setupUI() {
        // Cargar lista de idiomas desde la base de datos
        languageList = languageRepository.getAllLanguages()
        val languageNames = languageList.map { it.languageName }

        // Configurar el adaptador para el Spinner
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languageNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguages.adapter = adapter

        // Establecer idioma activo actual
        val activeLanguageIndex = languageList.indexOfFirst { it.isActive }
        if (activeLanguageIndex >= 0) {
            binding.spinnerLanguages.setSelection(activeLanguageIndex)
        }

        // Configurar el botón para actualizar el idioma
        binding.buttonChangeLanguage.setOnClickListener {
            val selectedPosition = binding.spinnerLanguages.selectedItemPosition
            val selectedLanguage = languageList[selectedPosition]

            if (languageRepository.updateActiveLanguage(selectedLanguage.id)) {
                changeAppLanguage(selectedLanguage.languageName)
                Toast.makeText(requireContext(), "Idioma cambiado a ${selectedLanguage.languageName}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error al cambiar el idioma", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changeAppLanguage(languageName: String) {
        val locale = when (languageName) {
            "Français" -> Locale.FRENCH
            "Español" -> Locale("es")
            "English" -> Locale.ENGLISH
            else -> Locale.ENGLISH
        }

        val config = Configuration(resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)

        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        requireActivity().recreate() // Recargar la actividad actual para aplicar el cambio
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
