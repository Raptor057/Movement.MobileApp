package com.essency.essencystockmovement.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object {
        private const val DATABASE_NAME = "EssencyStockMovement.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Aquí se crean las tablas usando sentencias SQL

        val createTableQueryAppUsers = """
            CREATE TABLE AppUsers (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            UserName TEXT NOT NULL,
            Name TEXT NOT NULL,
            LastName TEXT NOT NULL,
            Password TEXT NOT NULL,
            CreateUserDate DATETIME NOT NULL,
            IsAdmin BOOLEAN NOT NULL,
            Enable BOOLEAN NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTableQueryAppUsers)

        // *** Agregar usuario predeterminado ***
        val defaultUserInsert = """
        INSERT INTO AppUsers 
            (UserName, Name, LastName, Password, CreateUserDate, IsAdmin, Enable)
        VALUES
            ('Admin', 'System', 'Admin', 'Admin123*', datetime('now'), 1, 1);
    """.trimIndent()
        db.execSQL(defaultUserInsert)

        val createTableQueryAppConfigurationRegularExpression = """
            CREATE TABLE AppConfigurationRegularExpression (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            NameRegularExpression TEXT NOT NULL,
            RegularExpression TEXT NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTableQueryAppConfigurationRegularExpression)

        val createTableQueryAppConfigurationEmail = """
            CREATE TABLE AppConfigurationEmail (
            Email TEXT NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTableQueryAppConfigurationEmail)

        val createTableQueryLogEntry = """
            CREATE TABLE LogEntry (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            Timestamp DATETIME,
            LogLevel TEXT,
            Message TEXT,
            Exception TEXT
            );
        """.trimIndent()
        db.execSQL(createTableQueryLogEntry)

        val createTableQueryStockList = """
            CREATE TABLE StockList (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            IDStock INTEGER NOT NULL,
            Company TEXT NOT NULL,
            Source TEXT NOT NULL,
            SoucreLoc TEXT,
            Destination TEXT NOT NULL,
            DestinationLoc TEXT,
            PartNo TEXT NOT NULL,
            Rev TEXT NOT NULL,
            Lot TEXT NOT NULL,
            Qty INTEGER NOT NULL,
            Date TEXT NOT NULL,
            TimeStamp DATETIME NOT NULL,
            User TEXT NOT NULL,
            ContBolNum TEXT NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTableQueryStockList)

        val createTableQueryTraceabilityStockList = """
            CREATE TABLE TraceabilityStockList (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            IDStock INTEGER NOT NULL,
            Saved BOOLEAN NOT NULL,
            SendByEmail BOOLEAN NOT NULL,
            TimeStamp DATETIME NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTableQueryTraceabilityStockList)







    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Se llama al actualizar la versión de la DB.
        // Normalmente se eliminan o migran las tablas aquí.
        db.execSQL("DROP TABLE IF EXISTS AppUsers")
        db.execSQL("DROP TABLE IF EXISTS AppConfigurationRegularExpression")
        db.execSQL("DROP TABLE IF EXISTS AppConfigurationEmail")
        db.execSQL("DROP TABLE IF EXISTS LogEntry")
        db.execSQL("DROP TABLE IF EXISTS StockList")
        db.execSQL("DROP TABLE IF EXISTS TraceabilityStockList")
        onCreate(db)
    }

}