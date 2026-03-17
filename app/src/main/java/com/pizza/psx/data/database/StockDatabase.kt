package com.pizza.psx.data.database

import android.content.Context
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pizza.psx.domain.DAO.PortfolioModelDAO
import com.pizza.psx.domain.model.PortfolioModel
import com.pizza.psx.domain.model.Transaction
import java.util.concurrent.Executors


@Database(
    entities = [PortfolioModel::class,
                Transaction:: class
               ],
    version = 5,
    exportSchema = true,
)
abstract class StockDatabase: RoomDatabase() {
    abstract fun portfolioModelDAO(): PortfolioModelDAO



    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? =null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `transactions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `portfolioSymbol` TEXT NOT NULL,
                        `date` INTEGER,
                        `price` REAL,
                        `volume` INTEGER
                    )
                """.trimIndent())

            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Create new table with foreign key and index
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `transactions` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `portfolioSymbol` TEXT NOT NULL,
                `date` INTEGER,
                `price` REAL,
                `volume` INTEGER,
                FOREIGN KEY(`portfolioSymbol`) REFERENCES `PortfolioModel`(`symbol`) ON DELETE CASCADE
            )
        """.trimIndent())

                // 2. Copy data from the OLD table (note: backticks around `Transaction`)
                database.execSQL("""
            INSERT INTO transactions (id, portfolioSymbol, date, price, volume)
            SELECT id, portfolioSymbol, date, price, volume
            FROM `Transaction`
        """.trimIndent())

                // 3. Drop the old table
                database.execSQL("DROP TABLE IF EXISTS `Transaction`")

                // 4. Rename new table to final name
               // database.execSQL("ALTER TABLE transactions_new RENAME TO transactions")

                // 5. Create index on foreign key
                database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_transactions_portfolioSymbol 
            ON transactions(portfolioSymbol)
        """.trimIndent())
            }
        }

        val MIGRATION_2_4 = object : Migration(2, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the final transactions table
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `transactions` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `portfolioSymbol` TEXT NOT NULL,
                `date` INTEGER,
                `price` REAL,
                `volume` INTEGER,
                FOREIGN KEY(`portfolioSymbol`) REFERENCES `PortfolioModel`(`symbol`) ON DELETE CASCADE
            )
        """.trimIndent())
                // Create index
                database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_transactions_portfolioSymbol 
            ON transactions(portfolioSymbol)
        """.trimIndent())
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            ALTER TABLE transactions 
            ADD COLUMN transactionStatus TEXT
        """.trimIndent())
            }
        }

        fun getDatabase(context: Context): StockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_database"
                )
                    .addMigrations(MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_2_4,
                        MIGRATION_4_5
                        )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("PRAGMA foreign_keys=ON")
                        }
                    })
                    .setQueryCallback(RoomDatabase.QueryCallback { sqlQuery, bindArgs ->
                        println("SQL Query: $sqlQuery SQL Args: $bindArgs")
                        if (sqlQuery.contains("DELETE", ignoreCase = true) || sqlQuery.contains("REPLACE", ignoreCase = true)) {
                            Log.w("DB_CHECK", "SQL: $sqlQuery, Args: $bindArgs")
                        }
                    }, Executors.newSingleThreadExecutor())


                    .build()
                INSTANCE = instance
                val currentVersion = instance.getDBVersion()
                Log.d("DB_VERSION", "Current DB Version: $currentVersion")
                print("Current DB Version: $currentVersion")
                instance


            }


        }
    }
}

fun RoomDatabase.getDBVersion(): Int {
    return this.openHelper.readableDatabase.version
}