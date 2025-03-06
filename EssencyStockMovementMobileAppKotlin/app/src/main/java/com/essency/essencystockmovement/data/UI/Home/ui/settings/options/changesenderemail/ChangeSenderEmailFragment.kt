package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesenderemail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.EmailSender
import com.essency.essencystockmovement.data.repository.EmailRepository
import com.essency.essencystockmovement.data.repository.EmailSenderRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsEmailSenderBinding
import com.essency.essencystockmovement.data.UtilClass.EmailSenderService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeSenderEmailFragment : BaseFragment() {

    private var _binding: FragmentSettingsEmailSenderBinding? = null
    private val binding get() = _binding!!

    private lateinit var emailSenderRepository: EmailSenderRepository
    private lateinit var emailRepository: EmailRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsEmailSenderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa la base de datos y los repositorios
        val dbHelper = MyDatabaseHelper(requireContext())
        emailSenderRepository = EmailSenderRepository(dbHelper)
        emailRepository = EmailRepository(dbHelper)

        setupUI()

        return root
    }

    private fun setupUI() {
        // Cargar datos del remitente desde EmailSenderRepository
        val currentEmailSender = emailSenderRepository.getEmailSender()
        if (currentEmailSender != null) {
            binding.editTextEmail.setText(currentEmailSender.email)
            binding.editTextPassword.setText(currentEmailSender.password)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.settings_email_no_sender_config),
                Toast.LENGTH_SHORT
            ).show()
        }

        // Configurar el botón para actualizar el correo y contraseña del remitente
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

            // Actualizar datos en EmailSender
            val updatedEmailSender = EmailSender(
                id = currentEmailSender?.id ?: 0,
                email = newEmail,
                password = newPassword
            )

            val rowsUpdated = emailSenderRepository.updateEmailSender(updatedEmailSender)
            if (rowsUpdated > 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_email_updated_successfully),
                    Toast.LENGTH_SHORT
                ).show()

                // Enviar correo de prueba para confirmar actualización
                sendTestEmail(newEmail, newPassword)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_email_mail_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendTestEmail(senderEmail: String, senderPassword: String) {
        // Obtener el destinatario desde EmailRepository
        val recipientEmail = emailRepository.getEmail()?.email
        if (recipientEmail == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.settings_email_no_recipient_config),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Ejecutar el envío de correo en un hilo secundario
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val emailSender = EmailSenderService(senderEmail, senderPassword)

                val htmlBody = """
                    <html>
                        <body>
                            <h1 style="color: #6cccf4;">Essency Stock Movement
                            </h1>
                            <!-- <p>Este es un <strong>correo de prueba </strong></p> -->
                             <p>
                                Actualizacion de correo remitente.
                             </p>

                            <footer>
                                <p>
                                — Enviado Desde Essency Stock Movement Android App.
                                </p>
                            </footer>
                        </body>
                        </html>
            """.trimIndent()

                // Enviar correo
                emailSender.sendEmail(
                    to = recipientEmail,
                    subject = "Essency Stock Movement Android App",
                    body = htmlBody
                    //body = "Este es un correo enviado desde la configuración actualizada."
                )

                // Mostrar éxito en el hilo principal
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.settings_email_test_sent),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()

                // Mostrar error en el hilo principal
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.settings_email_test_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}