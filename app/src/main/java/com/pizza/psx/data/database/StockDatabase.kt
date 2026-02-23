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
    version = 4,
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
                    CREATE TABLE IF NOT EXISTS `Transaction` (
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

                // 1️⃣ Create new table with foreign key + index
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `transactions_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `portfolioSymbol` TEXT NOT NULL,
                `date` INTEGER,
                `price` REAL,
                `volume` INTEGER,
                FOREIGN KEY(`portfolioSymbol`) REFERENCES `PortfolioModel`(`symbol`) 
                ON DELETE CASCADE
            )
        """.trimIndent())

                // 2️⃣ Copy old data
                database.execSQL("""
            INSERT INTO transactions_new (id, portfolioSymbol, date, price, volume)
            SELECT id, portfolioSymbol, date, price, volume
            FROM `transactions`
        """.trimIndent())


                // 4️⃣ Rename new table
                database.execSQL("ALTER TABLE transactions_new RENAME TO transactions")

                // 5️⃣ Create index
                database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_transactions_portfolioSymbol 
            ON transactions(portfolioSymbol)
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
                    .addMigrations(MIGRATION_2_3,MIGRATION_3_4)
                    .setQueryCallback(RoomDatabase.QueryCallback { sqlQuery, bindArgs ->
                        println("SQL Query: $sqlQuery SQL Args: $bindArgs")
                    }, Executors.newSingleThreadExecutor())


                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}