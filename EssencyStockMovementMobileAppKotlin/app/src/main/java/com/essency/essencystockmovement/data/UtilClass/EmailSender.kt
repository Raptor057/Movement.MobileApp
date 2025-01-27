import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender(
    private val username: String, // Correo emisor
    private val password: String  // Contraseña o App Password
) {

    // Configura las propiedades necesarias para SMTP
    private fun getMailProperties(): Properties {
        return Properties().apply {
            put("mail.smtp.auth", "true")                  // Requiere autenticación
            put("mail.smtp.starttls.enable", "true")       // TLS habilitado
            put("mail.smtp.host", "smtp.gmail.com")        // Servidor SMTP de Gmail
            put("mail.smtp.port", "587")                  // Puerto para TLS
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
                setFrom(InternetAddress(username))         // Dirección emisor
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)) // Dirección destinatario
                setSubject(subject)                        // Asunto del correo
                setText(body)                              // Cuerpo del correo
            }
            Transport.send(message)
            println("Correo enviado exitosamente a $to")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al enviar el correo: ${e.message}")
        }
    }
}
