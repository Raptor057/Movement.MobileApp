package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesenderemail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.repository.EmailSenderRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsEmailSenderBinding

class ChangeSenderEmailFragment : BaseFragment() {

    private var _binding: FragmentSettingsEmailSenderBinding? = null
    private val binding get() = _binding!!

    private lateinit var emailSenderRepository: EmailSenderRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsEmailSenderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializar la base de datos y el repositorio
        val dbHelper = MyDatabaseHelper(requireContext())
        emailSenderRepository = EmailSenderRepository(dbHelper)

        setupUI()

        return root
    }

    private fun setupUI() {
        // Cargar el correo electrónico actual al iniciar
        val currentSenderEmail = emailSenderRepository.getEmailSender()
        binding.editTextEmail.setText(currentSenderEmail?.email)
        binding.editTextPassword.setText(currentSenderEmail?.password)

        // Configurar el botón de actualizar
        binding.buttonUpdateEmail.setOnClickListener {
            val newEmail = binding.editTextEmail.text.toString()
            val newPassword = binding.editTextPassword.text.toString()

            if (newEmail.isBlank() || newPassword.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_email_mail_not_empty),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val updated = emailSenderRepository.updateEmailSender(
                currentSenderEmail!!.copy(email = newEmail, password = newPassword)
            )

            if (updated > 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_email_updated_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_email_mail_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
