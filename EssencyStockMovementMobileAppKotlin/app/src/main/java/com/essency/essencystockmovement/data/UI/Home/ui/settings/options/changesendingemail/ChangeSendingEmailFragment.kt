//package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesendingemail
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import com.essency.essencystockmovement.R
//import com.essency.essencystockmovement.data.UI.BaseFragment
//import com.essency.essencystockmovement.data.UtilClass.EmailSenderService
//import com.essency.essencystockmovement.data.local.MyDatabaseHelper
//import com.essency.essencystockmovement.data.repository.EmailRepository
//import com.essency.essencystockmovement.data.repository.EmailSenderRepository
//import com.essency.essencystockmovement.databinding.FragmentSettingsEmailBinding
//
//class ChangeSendingEmailFragment : BaseFragment() {
//
//    private var _binding: FragmentSettingsEmailBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var emailRepository: EmailRepository
//    private lateinit var emailSenderService: EmailSenderService
//    private var moduleName = "Update Sending Email"
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentSettingsEmailBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        // Inicializa la base de datos y el repositorio
//        val dbHelper = MyDatabaseHelper(requireContext())
//        emailRepository = EmailRepository(dbHelper)
//
//        // 🔹 Ejemplo de inicialización del EmailSenderService
//        //    1) Obtenemos el correo y password de la tabla EmailSender (si corresponde)
//        val emailSenderRepo = EmailSenderRepository(dbHelper)
//        val senderData = emailSenderRepo.getEmailSender()
//        if (senderData != null) {
//            // 2) Creamos la instancia con el correo y la contraseña
//            emailSenderService = EmailSenderService(
//                username = senderData.email,
//                password = senderData.password
//            )
//        } else {
//            // Si no hay datos en la tabla EmailSender, mostrar error o asignar valores fijos
//            // Por ejemplo:
//            emailSenderService = EmailSenderService("", "")
//        }
//
//        setupUI()
//
//        return root
//    }
//
//    private fun setupUI() {
//        // Cargar el correo electrónico actual al iniciar
//        val currentEmail = emailRepository.getEmail()?.email
//        binding.editTextEmail.setText(currentEmail)
//
//        // Configurar el botón para actualizar el correo electrónico
//        binding.buttonUpdateEmail.setOnClickListener {
//            val newEmail = binding.editTextEmail.text.toString()
//
//            if (newEmail.isBlank()) {
//                Toast.makeText(
//                    requireContext(),
//                    getString(R.string.settings_email_mail_not_empty),
//                    Toast.LENGTH_SHORT
//                ).show()
//                return@setOnClickListener
//            }
//
//            val isUpdated = emailRepository.updateEmail(newEmail)
//
//            if (isUpdated) {
//                Toast.makeText(
//                    requireContext(),
//                    getString(R.string.settings_email_updated_successfully),
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                val newEmail = emailRepository.getEmail()?.email.toString()
//                val htmlBody = """
//                    <html>
//                        <body>
//                            <h1 style="color: #6cccf4;">Essency Stock Movement
//                            </h1>
//                            <!-- <p>Este es un <strong>correo de prueba </strong></p> -->
//                             <p>
//                                Actualizacion de correo envio.
//                             </p>
//
//                            <footer>
//                                <p>
//                                — Enviado Desde Essency Stock Movement Android App.
//                                </p>
//                            </footer>
//                        </body>
//                        </html>
//            """.trimIndent()
//                emailSenderService.sendEmail(newEmail,moduleName,htmlBody)
//
//            } else {
//                Toast.makeText(
//                    requireContext(),
//                    getString(R.string.settings_email_mail_error),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesendingemail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.UtilClass.EmailSenderService
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.repository.EmailRepository
import com.essency.essencystockmovement.data.repository.EmailSenderRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsEmailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeSendingEmailFragment : BaseFragment() {

    private var _binding: FragmentSettingsEmailBinding? = null
    private val binding get() = _binding!!

    private lateinit var emailRepository: EmailRepository
    private lateinit var emailSenderService: EmailSenderService
    private var moduleName = "Update Sending Email"

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

        // Inicializa el EmailSenderService (remitente y password)
        val emailSenderRepo = EmailSenderRepository(dbHelper)
        val senderData = emailSenderRepo.getEmailSender()
        if (senderData != null) {
            emailSenderService = EmailSenderService(
                username = senderData.email,
                password = senderData.password
            )
        } else {
            // Por si no existe configuración en la DB
            emailSenderService = EmailSenderService("", "")
        }

        setupUI()
        return root
    }

    private fun setupUI() {
        // Cargar el correo electrónico actual al iniciar
        val currentEmail = emailRepository.getEmail()?.email
        binding.editTextEmail.setText(currentEmail)

        // Botón para actualizar el correo electrónico
        binding.buttonUpdateEmail.setOnClickListener {
            val newEmail = binding.editTextEmail.text.toString()

            if (newEmail.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_email_mail_not_empty),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val isUpdated = emailRepository.updateEmail(newEmail)
            if (isUpdated) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_email_updated_successfully),
                    Toast.LENGTH_SHORT
                ).show()

                // Preparar datos para el correo
                val newEmailValue = emailRepository.getEmail()?.email.orEmpty()
                val htmlBody = """
                    <html>
                        <body>
                            <h1 style="color: #6cccf4;">Essency Stock Movement</h1>
                            <p>Actualizacion de correo envio.</p>
                            <footer>
                                <p>— Enviado Desde Essency Stock Movement Android App.</p>
                            </footer>
                        </body>
                    </html>
                """.trimIndent()

                // 1) Lanzar una corrutina en IO para enviar el correo
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        emailSenderService.sendEmail(
                            to = newEmailValue,
                            subject = moduleName,
                            body = htmlBody
                        )
                        // 2) Volver a Main thread para mostrar un Toast de éxito
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Correo enviado a $newEmailValue", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // 3) Si hay error, también volver a Main thread
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Error al enviar correo: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

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
