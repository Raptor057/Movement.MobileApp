package com.essency.essencystockmovement.data.local

import android.app.Application

class InitializationDatabaseHelper : Application()
{

    override fun onCreate() {
        super.onCreate()
        // Inicializa tu DB Helper
        //dbHelper = MyDatabaseHelper(this)
        // Aquí forzamos la creación de la DB, si quieres
        val dbHelper = MyDatabaseHelper(this)
        // Esto fuerza la creación de la base de datos en disco (si no existe)
        dbHelper.writableDatabase.close()
    }
}