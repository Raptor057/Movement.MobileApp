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
            UserType TEXT NOT NULL,
            PasswordHash TEXT NOT NULL,
            Salt TEXT NOT NULL,
            CreateUserDate DATETIME NOT NULL,
            IsAdmin BOOLEAN NOT NULL,
            Enable BOOLEAN NOT NULL);
        """.trimIndent()
        db.execSQL(createTableQueryAppUsers)

        // *** Agregar usuario predeterminado ***
        /*
        * Usuario: Admin
        * Password: Admin123*
        */
        val defaultUserInsert = """
        INSERT INTO AppUsers (UserName, Name, LastName,UserType, PasswordHash, Salt,CreateUserDate, IsAdmin, Enable)
        VALUES ('Admin', 'System', 'Admin','Diligent', 'OPeT4bF/+q1SLsjOaiAILe2aYGJDIzWwnbqL2W7XhEU=','hXzGR5D26k8de0CHXzp4kA==', datetime('now'), 1, 1);
    """.trimIndent()
        db.execSQL(defaultUserInsert)

        val createTableQueryAppConfigurationRegularExpression = """
            CREATE TABLE AppConfigurationRegularExpression (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            NameRegularExpression TEXT NOT NULL,
            RegularExpression TEXT NOT NULL);
        """.trimIndent()
        db.execSQL(createTableQueryAppConfigurationRegularExpression)

        val defaultRegularExpression = """
        INSERT INTO AppConfigurationRegularExpression (NameRegularExpression, RegularExpression)
        VALUES ('Pallet', '[a-z]+'),('Individual', '[a-z]+');
    """.trimIndent()
        db.execSQL(defaultRegularExpression)


        val createTableQueryAppConfigurationEmail = """
            CREATE TABLE AppConfigurationEmail (
            Email TEXT NOT NULL);
        """.trimIndent()
        db.execSQL(createTableQueryAppConfigurationEmail)

        val defaultEmailInsert = """
        INSERT INTO AppConfigurationEmail (Email)
        VALUES ('r.arriaga@generaltransmissions.com');
    """.trimIndent()
        db.execSQL(defaultEmailInsert)

        val createTableQueryLogEntry = """
            CREATE TABLE LogEntry (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            Timestamp DATETIME,
            LogLevel TEXT,
            Message TEXT,
            Exception TEXT);
        """.trimIndent()
        db.execSQL(createTableQueryLogEntry)

        val createTableQueryStockList = """
            CREATE TABLE StockList (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            --IDStock INTEGER NOT NULL,
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
            ContBolNum TEXT NOT NULL);
        """.trimIndent()
        db.execSQL(createTableQueryStockList)

        val createTableQueryTraceabilityStockList = """
            CREATE TABLE TraceabilityStockList (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- Identificador único
            --IDStock INTEGER NOT NULL,                     -- Relación con StockList BOL #
            BatchNumber TEXT NOT NULL,                             -- Número de lote o batch para identificar el grupo
            MovementType TEXT NOT NULL,                            -- Tipo de movimiento asociado (ejemplo: RECEIVING, SHIPMENT, INVENTORY)
            NumberOfHeaters INTEGER NOT NULL, 
            NumberOfHeatersFinished  INTEGER NOT NULL,
            Finish BOOLEAN NOT NULL,                    -- Indica si el movimiento ha sido guardado en StockList
            SendByEmail BOOLEAN NOT NULL,                 -- Indica si el movimiento se envió por correo electrónico
            CreatedBy TEXT,                               -- Usuario que creó el registro
            TimeStamp DATETIME NOT NULL,                  -- Marca de tiempo del registro
            Notes TEXT);                                    -- Notas adicionales o comentarios
        """.trimIndent()
        db.execSQL(createTableQueryTraceabilityStockList)

        val createTableQueryMovementType = """
            CREATE TABLE MovementType (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            UserType TEXT NOT NULL,
            Type TEXT NOT NULL,
            Source TEXT NOT NULL,
            Destination TEXT NOT NULL);
        """.trimIndent()
        db.execSQL(createTableQueryMovementType)

        // *** Agregar movementType predeterminado  ***
        val insertMovementType = """
            INSERT INTO MovementType (UserType, Type, Source, Destination)
            VALUES 
            ('Diligent', 'RECEIVING', 'TRANSIT', 'DILIGEN'),
            ('Diligent', 'PREPARATION SHIPMENT', 'DILIGEN', 'PRT'),
            ('Diligent', 'INVENTARIO', 'DILIGEN', 'N/A'),
            ('GTFR', 'PREPARATION SHIPMENT', 'N/A', 'N/A'),
            ('GTFR', 'INVENTARIO', 'N/A', 'N/A');
        """.trimIndent()
        db.execSQL(insertMovementType)

        val createTableQueryLanguage = """
            CREATE TABLE Language(
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            LanguageName TEXT NOT NULL,
            ActiveLanguage BOOLEAN NOT NULL);
        """.trimIndent()
        db.execSQL(createTableQueryLanguage)

        // *** Agregar lenguaje predeterminado  ***
        val insertLanguage = """
        INSERT INTO Language (LanguageName,ActiveLanguage)
        VALUES ('Français',0),('Español',0),('English',1);
        """.trimIndent()
        db.execSQL(insertLanguage)

        val createTableQueryWarehouseList = """
            CREATE TABLE WarehouseList(
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            Warehouse TEXT NOT NULL);
        """.trimIndent()
        db.execSQL(createTableQueryWarehouseList)

        // *** Agregar lenguaje predeterminado  ***
        val insertWarehouseList = """
        INSERT INTO WarehouseList (Warehouse)
        VALUES ('Diligent'),('GTFR');
        """.trimIndent()
        db.execSQL(insertWarehouseList)

        val createTableQueryEmailSender = """
            CREATE TABLE EmailSender(
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            Email TEXT NOT NULL,
            Password TEXT NOT NULL);
        """.trimIndent()
        db.execSQL(createTableQueryEmailSender)

        // *** Agregar lenguaje predeterminado  ***
        val insertEmailSender = """
        INSERT INTO EmailSender (Email,Password)
        VALUES ('essency.diligent@gmail.com','mpsb kyvu wocg bjjf');
        """.trimIndent()
        db.execSQL(insertEmailSender)

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
        db.execSQL("DROP TABLE IF EXISTS MovementType")
        db.execSQL("DROP TABLE IF EXISTS Language")
        db.execSQL("DROP TABLE IF EXISTS WarehouseList")
        db.execSQL("DROP TABLE IF EXISTS EmailSender")
        onCreate(db)
    }

}