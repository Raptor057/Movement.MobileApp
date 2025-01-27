package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesendingemail

import EmailSender
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.repository.EmailRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsEmailBinding

class ChangeSendingEmailFragment : BaseFragment() {//Fragment() {

    private var _binding: FragmentSettingsEmailBinding? = null
    private val binding get() = _binding!!

    private lateinit var emailRepository: EmailRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsEmailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa la base de datos y el repositorio
        val dbHelper = MyDatabaseHelper(requireContext())
        emailRepository = EmailRepository(dbHelper)

        setupUI()

        return root
    }

    private fun setupUI() {
        // Cargar el correo electrónico actual al iniciar
        val currentEmail = emailRepository.getEmail()?.email

        // Configura tu correo y contraseña o App Password
        val senderEmail = "essency.diligent@gmail.com"
        val senderPassword = "Z2N&gR+7zFHc" // Usa un App Password para mayor seguridad Z2N&gR+7zFHc

        // Crea una instancia de EmailSender
        val emailSender = EmailSender(senderEmail, senderPassword)

        binding.editTextEmail.setText(currentEmail)

        // Configurar el botón para actualizar el correo electrónico
        binding.buttonUpdateEmail.setOnClickListener {
            val newEmail = binding.editTextEmail.text.toString()

            if (newEmail.isBlank()) {
                Toast.makeText(requireContext(),
                    context?.getString(R.string.settings_email_mail_not_empty) ?: "", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isUpdated = emailRepository.updateEmail(newEmail)

            if (isUpdated) {
                Toast.makeText(requireContext(), context?.getString(R.string.settings_email_updated_successfully) ?: "", Toast.LENGTH_SHORT).show()
                // Envía un correo
                emailSender.sendEmail(
                    to = "destinatario@example.com",   // Dirección de destino
                    subject = "Correo de prueba",      // Asunto
                    body = "Este es un correo enviado desde una app Android usando Gmail." // Cuerpo
                )

            } else {
                Toast.makeText(requireContext(), context?.getString(R.string.settings_email_mail_error) ?: "", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
