package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.EmailSender

interface IEmailSenderRepository {
    fun getEmailSender(): EmailSender?
    fun updateEmailSender(emailSender: EmailSender): Int
}