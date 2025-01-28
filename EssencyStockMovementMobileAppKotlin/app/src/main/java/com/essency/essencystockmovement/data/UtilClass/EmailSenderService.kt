import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import com.sun.mail.smtp.SMTPTransport

class EmailSenderService(
    private val username: String, // Correo emisor
    private val password: String  // Contraseña o App Password
) {

    // Configura las propiedades necesarias para SMTP usando STARTTLS
    private fun getMailProperties(): Properties {
        return Properties().apply {
            put("mail.transport.protocol", "smtp")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")        // Habilitar STARTTLS
            put("mail.smtp.host", "smtp.gmail.com")         // Servidor SMTP de Gmail
            put("mail.smtp.port", "587")                   // Puerto STARTTLS
            put("mail.smtp.ssl.protocols", "TLSv1.1 TLSv1.2") // Protocolos seguros TLS
            put("mail.smtp.ssl.trust", "smtp.gmail.com")    // Confiar en el host
            put("mail.debug", "true")                      // Habilitar depuración para rastreo
        }
    }

    // Crea la sesión de correo con autenticación
    private fun createSession(): Session {
        return Session.getInstance(getMailProperties(), object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })
    }

    // Método para enviar correos
    fun sendEmail(to: String, subject: String, body: String) {
        try {
            val session = createSession()
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username))            // Dirección emisor
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)) // Dirección destinatario
                setSubject(subject)                           // Asunto del correo
                //setText(body)                                 // Cuerpo del correo Texto
                setContent(body, "text/html; charset=utf-8") // Establecer el cuerpo como HTML
            }

            // Enviar el correo usando el transporte SMTP
            val transport: SMTPTransport = session.getTransport("smtp") as SMTPTransport
            transport.connect("smtp.gmail.com", username, password)
            transport.sendMessage(message, message.allRecipients)
            transport.close()

            println("Correo enviado exitosamente a $to")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al enviar el correo: ${e.message}")
        }
    }
}
