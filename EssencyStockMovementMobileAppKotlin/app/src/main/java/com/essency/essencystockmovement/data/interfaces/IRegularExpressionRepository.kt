package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.AppConfigurationRegularExpression

interface IRegularExpressionRepository {
    /**
     * Obtiene todas las expresiones regulares configuradas.
     * @return Lista de AppConfigurationRegularExpression.
     */
    fun getAllRegularExpressions(): List<AppConfigurationRegularExpression>

    /**
     * Actualiza únicamente la expresión regular existente.
     * @param id ID de la expresión regular a actualizar.
     * @param regularExpression Nueva cadena que representa la expresión regular.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    fun updateRegularExpression(id: Int, regularExpression: String): Boolean
}