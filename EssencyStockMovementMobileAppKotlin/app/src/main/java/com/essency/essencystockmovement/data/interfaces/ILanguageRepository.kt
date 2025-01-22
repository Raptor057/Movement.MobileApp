package com.essency.essencystockmovement.data.interfaces

import org.intellij.lang.annotations.Language

interface ILanguageRepository {
    /**
     * Obtiene todos los idiomas disponibles.
     * @return Lista de idiomas configurados.
     */
    fun getAllLanguages(): MutableList<com.essency.essencystockmovement.data.model.Language>

    /**
     * Actualiza el idioma activo.
     * @param id ID del idioma que se quiere establecer como activo.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    fun updateActiveLanguage(id: Int): Boolean
}