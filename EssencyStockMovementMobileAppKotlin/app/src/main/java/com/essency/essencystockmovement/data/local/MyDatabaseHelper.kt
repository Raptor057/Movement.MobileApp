package com.essency.essencystockmovement.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object {
        private const val DATABASE_NAME = "EssencyStockMovement.db"
        private const val DATABASE_VERSION = 9
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
        VALUES ('r.arriaga@generaltransmissions.com,f.guerrier@generaltransmissions.com');
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
            IDTraceabilityStockList INTEGER NOT NULL,
            Company TEXT NOT NULL,
            Source TEXT NOT NULL,
            SourceLoc TEXT,
            Destination TEXT NOT NULL,
            DestinationLoc TEXT,
            Pallet TEXT,  -- 🔹 Nuevo campo agregado
            PartNo TEXT NOT NULL,
            Rev TEXT NOT NULL,
            Lot TEXT NOT NULL,
            Qty INTEGER NOT NULL,
            ProductionDate TEXT,  -- 🔹 Nuevo campo agregado
            CountryOfProduction TEXT,  -- 🔹 Nuevo campo agregado
            SerialNumber TEXT,  -- 🔹 Nuevo campo agregado
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
            BatchNumber TEXT NOT NULL,
            MovementType TEXT NOT NULL,
            NumberOfHeaters INTEGER NOT NULL,
            NumberOfHeatersFinished INTEGER NOT NULL,
            Finish INTEGER NOT NULL DEFAULT 0,
            SendByEmail INTEGER NOT NULL DEFAULT 0,
            CreatedBy TEXT,
            Source TEXT NOT NULL,   -- 🔹 Asegúrate de que esta línea existe
            Destination TEXT NOT NULL,
            TimeStamp DATETIME NOT NULL,
            Notes TEXT
            );
        """.trimIndent()
        db.execSQL(createTableQueryTraceabilityStockList)

        //---------------
        val createTableQueryAuditStockList = """
            CREATE TABLE AuditStockList (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            IDTraceabilityStockList INTEGER NOT NULL,
            Company TEXT NOT NULL,
            Source TEXT NOT NULL,
            SourceLoc TEXT,
            Destination TEXT NOT NULL,
            DestinationLoc TEXT,
            Pallet TEXT,  -- 🔹 Nuevo campo agregado
            PartNo TEXT NOT NULL,
            Rev TEXT NOT NULL,
            Lot TEXT NOT NULL,
            Qty INTEGER NOT NULL,
            ProductionDate TEXT,  -- 🔹 Nuevo campo agregado
            CountryOfProduction TEXT,  -- 🔹 Nuevo campo agregado
            SerialNumber TEXT,  -- 🔹 Nuevo campo agregado
            Date TEXT NOT NULL,
            TimeStamp DATETIME NOT NULL,
            User TEXT NOT NULL,
            ContBolNum TEXT NOT NULL
        );
        """.trimIndent()
        db.execSQL(createTableQueryStockList)

        val createTableQueryAuditTraceabilityStockList = """
            CREATE TABLE AuditTraceabilityStockList (
            ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            BatchNumber TEXT NOT NULL,
            MovementType TEXT NOT NULL,
            NumberOfHeaters INTEGER NOT NULL,
            NumberOfHeatersFinished INTEGER NOT NULL,
            Finish INTEGER NOT NULL DEFAULT 0,
            SendByEmail INTEGER NOT NULL DEFAULT 0,
            CreatedBy TEXT,
            Source TEXT NOT NULL,   -- 🔹 Asegúrate de que esta línea existe
            Destination TEXT NOT NULL,
            TimeStamp DATETIME NOT NULL,
            Notes TEXT
            );
        """.trimIndent()
        db.execSQL(createTableQueryTraceabilityStockList)

        //---------------

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
            ('Diligent', 'RECEIVING', 'TRANSI', 'DILIGE'),
            ('Diligent', 'PREPARATION SHIPMENT', 'DILIGE', 'PRT'),
            ('Diligent', 'INVENTARIO', 'DILIGE', 'N/A'),
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

        // *** Agregar WarehouseList predeterminado  ***
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
        VALUES ('essency.diligent@gmail.com','rkze uawr cxvs rxai');
        """.trimIndent()
        db.execSQL(insertEmailSender)

        val createUserMovementsView = """
        CREATE VIEW UserMovementsView AS
        SELECT 
        Au.UserName, Au.Name, Au.LastName, Au.IsAdmin, 
        MT.Type, WL.Warehouse, MT.Source, MT.Destination
        FROM AppUsers Au
        INNER JOIN WarehouseList WL ON Au.UserType = WL.Warehouse
        INNER JOIN MovementType MT ON MT.UserType = WL.Warehouse;
        """.trimIndent()
        db.execSQL(createUserMovementsView)

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
        db.execSQL("DROP VIEW IF EXISTS UserMovementsView")
        onCreate(db)
    }

}