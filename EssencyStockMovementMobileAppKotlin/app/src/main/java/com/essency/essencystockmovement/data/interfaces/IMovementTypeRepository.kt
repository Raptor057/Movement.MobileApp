package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.MovementType
import com.essency.essencystockmovement.data.model.MovementTypeDestination

interface IMovementTypeRepository {
    /**
     * Obtiene todos los tipos de movimiento.
     * @return Lista de tipos de movimiento.
     */
    fun getAllMovementTypes(): List<MovementType>

    /**
     * Inserta un nuevo tipo de movimiento.
     * @param movementType Tipo de movimiento a insertar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    fun insertMovementType(movementType: MovementType): Boolean

    /**
     * Actualiza un tipo de movimiento existente.
     * @param movementType Tipo de movimiento con la información actualizada.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    fun updateMovementType(movementType: MovementType): Boolean

    /**
     * Elimina un tipo de movimiento por su ID.
     * @param id ID del tipo de movimiento a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    fun deleteMovementTypeById(id: Int): Boolean

    fun getDestinationInMovementTypesByTypeandUserType(Type: String, UserType: String): String

    /**
     * Obtiene el tipo de movimiento por su ID.
     * */
    fun getSourceInMovementTypesByTypeandUserType(Type: String, UserType: String): String

    fun getDestinationInMovementTypesByID(): List<MovementTypeDestination>

    fun updateDestinationInMovementTypeByID(Destination: String, id: Int): Boolean


}