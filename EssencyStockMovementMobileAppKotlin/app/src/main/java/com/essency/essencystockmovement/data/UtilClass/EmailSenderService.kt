package com.essency.essencystockmovement.data.UtilClass

import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import com.sun.mail.smtp.SMTPTransport
import javax.activation.DataHandler
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

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

    /**
     * 2) Enviar correo con adjunto
     * @param attachmentName   Nombre del archivo adjunto (por ej. "Reporte.txt")
     * @param attachmentContent Contenido del archivo en texto (lo convertiremos internamente a ByteArray).
     */
    fun sendEmailWithAttachment(
        to: String,
        subject: String,
        body: String,
        attachmentName: String,
        attachmentContent: String
    ) {
        try {
            val session = createSession()
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                setSubject(subject)
            }

            // 1) Parte de texto (HTML)
            val textBodyPart = MimeBodyPart()
            textBodyPart.setContent(body, "text/html; charset=utf-8")

            // 2) Parte adjunta (ByteArrayDataSource con MIME "text/plain", por ejemplo)
            val attachmentBodyPart = MimeBodyPart()
            val byteArray = attachmentContent.toByteArray()  // Convertimos el String a bytes
            val dataSource = ByteArrayDataSource(byteArray, "text/plain")
            attachmentBodyPart.dataHandler = DataHandler(dataSource)
            attachmentBodyPart.fileName = attachmentName

            // 3) Unir ambas partes en un MimeMultipart
            val multipart = MimeMultipart().apply {
                addBodyPart(textBodyPart)
                addBodyPart(attachmentBodyPart)
            }

            // 4) Asignar el multipart al mensaje
            message.setContent(multipart)

            // 5) Conectar y enviar
            val transport = session.getTransport("smtp") as SMTPTransport
            transport.connect("smtp.gmail.com", username, password)
            transport.sendMessage(message, message.allRecipients)
            transport.close()

            println("Correo con adjunto enviado exitosamente a $to")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al enviar el correo con adjunto: ${e.message}")
        }
    }
}
