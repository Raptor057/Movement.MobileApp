package com.essency.essencystockmovement.data.repository

import android.content.ContentValues
import android.database.Cursor
import android.util.Base64
import com.essency.essencystockmovement.data.UtilClass.PBKDF2Helper
import com.essency.essencystockmovement.data.interfaces.IAppUserRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AppUser

class AppUserRepository(private val dbHelper: MyDatabaseHelper) : IAppUserRepository {

    override fun insert(appUser: AppUser): Long {
        val db = dbHelper.writableDatabase

        // 1. Generar un salt aleatorio
        val salt = PBKDF2Helper.generateSalt()

        // 2. Obtener el hash de la contraseña con ese salt
        val hashBytes = PBKDF2Helper.hashPassword(appUser.passwordHash, salt)

        // 3. Convertir ambos a Base64 (u otro formato, p. ej. hex) para guardarlos en la DB
        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashBase64 = Base64.encodeToString(hashBytes, Base64.NO_WRAP)

        val values = ContentValues().apply {
            put("UserName", appUser.userName)
            put("Name", appUser.name)
            put("LastName", appUser.lastName)
            put("UserType", appUser.userType) // Nuevo campo agregado
            put("PasswordHash", hashBase64)
            put("Salt", saltBase64)
            put("CreateUserDate", appUser.createUserDate)
            put("IsAdmin", if (appUser.isAdmin) 1 else 0)
            put("Enable", if (appUser.enable) 1 else 0)
        }

        val idInserted = db.insert("AppUsers", null, values)
        db.close()
        return idInserted
    }

    override fun getAll(): List<AppUser> {
        val db = dbHelper.readableDatabase
        val userList = mutableListOf<AppUser>()
        val query = "SELECT * FROM AppUsers"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                userList.add(cursorToAppUser(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    override fun getById(id: Int): AppUser? {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM AppUsers WHERE ID = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        var appUser: AppUser? = null
        if (cursor.moveToFirst()) {
            appUser = cursorToAppUser(cursor)
        }
        cursor.close()
        db.close()
        return appUser
    }

    /*
    Cómo recuperar los datos del usuario en otra actividad
Si necesitas acceder a los datos del usuario en otra parte de la app, por ejemplo en HomeActivity, usa:


val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
val userID = sharedPreferences.getInt("userID", -1) // -1 indica que no hay usuario logueado
val userName = sharedPreferences.getString("userName", "")
val userType = sharedPreferences.getString("userType", "")
val isAdmin = sharedPreferences.getBoolean("isAdmin", false)
println("Usuario logueado: $userName, Tipo: $userType, Admin: $isAdmin")

    */
    override fun getByUserName(userName: String): AppUser? {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM AppUsers WHERE UserName = ?"
        val cursor = db.rawQuery(query, arrayOf(userName))

        var appUser: AppUser? = null
        if (cursor.moveToFirst()) {
            appUser = cursorToAppUser(cursor)
        }

        cursor.close()
        db.close()
        return appUser
    }


    override fun update(appUser: AppUser): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("UserName", appUser.userName)
            put("Name", appUser.name)
            put("LastName", appUser.lastName)
            put("UserType", appUser.userType) // Nuevo campo agregado
            put("PasswordHash", appUser.passwordHash)
            put("Salt", appUser.salt)
            put("CreateUserDate", appUser.createUserDate)
            put("IsAdmin", if (appUser.isAdmin) 1 else 0)
            put("Enable", if (appUser.enable) 1 else 0)
        }
        val rowsAffected = db.update(
            "AppUsers",
            values,
            "ID = ?",
            arrayOf(appUser.id.toString())
        )
        db.close()
        return rowsAffected
    }

    override fun deleteById(id: Int): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("AppUsers", "ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    override fun login(username: String, plainPassword: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT Salt, PasswordHash FROM AppUsers WHERE UserName = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        if (cursor.moveToFirst()) {
            val saltBase64 = cursor.getString(cursor.getColumnIndexOrThrow("Salt"))
            val hashBase64 = cursor.getString(cursor.getColumnIndexOrThrow("PasswordHash"))
            cursor.close()
            db.close()

            // Decodificar para recuperar salt y hash almacenado
            val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
            val storedHash = Base64.decode(hashBase64, Base64.NO_WRAP)

            // Re-hashear la contraseña ingresada
            val newHash = PBKDF2Helper.hashPassword(plainPassword, salt)

            // Comparar ambos
            return newHash.contentEquals(storedHash)
        } else {
            cursor.close()
            db.close()
            return false
        }
    }


    /**
     * Función utilitaria que convierte un cursor en AppUser
     */
    private fun cursorToAppUser(cursor: Cursor): AppUser {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
        val userName = cursor.getString(cursor.getColumnIndexOrThrow("UserName"))
        val name = cursor.getString(cursor.getColumnIndexOrThrow("Name"))
        val lastName = cursor.getString(cursor.getColumnIndexOrThrow("LastName"))
        val userType = cursor.getString(cursor.getColumnIndexOrThrow("UserType")) // Nuevo campo agregado
        val passwordHash = cursor.getString(cursor.getColumnIndexOrThrow("PasswordHash"))
        val salt = cursor.getString(cursor.getColumnIndexOrThrow("Salt"))
        val createUserDate = cursor.getString(cursor.getColumnIndexOrThrow("CreateUserDate"))
        val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("IsAdmin")) == 1
        val enable = cursor.getInt(cursor.getColumnIndexOrThrow("Enable")) == 1

        return AppUser(
            id = id,
            userName = userName,
            name = name,
            lastName = lastName,
            userType = userType, // Nuevo campo agregado
            passwordHash = passwordHash,
            salt = salt,
            createUserDate = createUserDate,
            isAdmin = isAdmin,
            enable = enable
        )
    }
}
