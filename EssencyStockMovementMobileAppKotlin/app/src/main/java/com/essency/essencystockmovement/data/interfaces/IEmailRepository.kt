package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.AppConfigurationEmail

interface IEmailRepository {
    /**
     * Obtiene la configuración de email actual.
     * @return AppConfigurationEmail o null si no hay registros.
     */
    fun getEmail(): AppConfigurationEmail?

    /**
     * Actualiza la dirección de email en la configuración.
     * @param newEmail Nueva dirección de email a establecer.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    fun updateEmail(newEmail: String): Boolean
}
