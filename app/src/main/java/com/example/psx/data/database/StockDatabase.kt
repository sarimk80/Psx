package com.example.psx.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.psx.domain.DAO.PortfolioModelDAO
import com.example.psx.domain.model.PortfolioModel


@Database(
    entities = [PortfolioModel::class],
    version = 1,
    exportSchema = false
)
abstract class StockDatabase: RoomDatabase() {
    abstract fun portfolioModelDAO(): PortfolioModelDAO

    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? =null

        fun getDatabase(context: Context): StockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}