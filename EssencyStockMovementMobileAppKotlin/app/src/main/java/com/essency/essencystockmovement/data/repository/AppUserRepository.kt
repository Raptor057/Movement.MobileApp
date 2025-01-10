package com.essency.essencystockmovement.data.repository


import android.content.ContentValues
import android.database.Cursor
import com.essency.essencystockmovement.data.interfaces.IAppUserRepository
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.AppUser

class AppUserRepository(private val dbHelper: MyDatabaseHelper) : IAppUserRepository {

    override fun insert(appUser: AppUser): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("UserName", appUser.userName)
            put("Name", appUser.name)
            put("LastName", appUser.lastName)
            put("Password", appUser.password)
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

    override fun update(appUser: AppUser): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("UserName", appUser.userName)
            put("Name", appUser.name)
            put("LastName", appUser.lastName)
            put("Password", appUser.password)
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

    /**
     * Función utilitaria que convierte un cursor en AppUser
     */
    private fun cursorToAppUser(cursor: Cursor): AppUser {
        // Ojo con getColumnIndexOrThrow para lanzar excepción si no existe la columna
        val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
        val userName = cursor.getString(cursor.getColumnIndexOrThrow("UserName"))
        val name = cursor.getString(cursor.getColumnIndexOrThrow("Name"))
        val lastName = cursor.getString(cursor.getColumnIndexOrThrow("LastName"))
        val password = cursor.getString(cursor.getColumnIndexOrThrow("Password"))
        val createUserDate = cursor.getString(cursor.getColumnIndexOrThrow("CreateUserDate"))
        val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("IsAdmin")) == 1
        val enable = cursor.getInt(cursor.getColumnIndexOrThrow("Enable")) == 1

        return AppUser(
            id = id,
            userName = userName,
            name = name,
            lastName = lastName,
            password = password,
            createUserDate = createUserDate,
            isAdmin = isAdmin,
            enable = enable
        )
    }
}
