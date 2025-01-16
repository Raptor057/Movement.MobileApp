/*
Explicación rápida
generateSalt(): Crea un arreglo de bytes aleatorios con SecureRandom. Esta semilla (salt) ayuda a que cada usuario tenga un hash diferente (aunque tengan la misma contraseña).
hashPassword():
Convierte la contraseña a char[] y la combina con el salt, con ITERATIONS repeticiones y produciendo una clave de KEY_LENGTH bits.
Usa "PBKDF2WithHmacSHA256" (puedes cambiarlo si tu entorno no lo soporta o deseas otro).
validatePassword():
Genera un nuevo hash con la misma función y salt.
Compara con el hash almacenado usando contentEquals.


 */

package com.essency.essencystockmovement.data.UtilClass

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PBKDF2Helper {
    private const val ITERATIONS = 65536     // Número de iteraciones, ajusta según tus necesidades
    private const val KEY_LENGTH = 256      // Tamaño en bits de la clave resultante (256 = 32 bytes)

    /**
     * Genera un salt aleatorio de 16 bytes (puedes usar 16, 32, etc.)
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

    /**
     * Hashea la contraseña usando PBKDF2 con HmacSHA256.
     * @param password Contraseña en texto plano (no la guardes nunca en DB).
     * @param salt Salt aleatorio, recomendable almacenarlo junto al hash en la DB.
     */
    fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val spec: KeySpec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    /**
     * Valida una contraseña comparando el hash esperado (almacenado) con el hash recalculado.
     */
    fun validatePassword(password: String, salt: ByteArray, expectedHash: ByteArray): Boolean {
        val newHash = hashPassword(password, salt)
        return newHash.contentEquals(expectedHash)
    }
}