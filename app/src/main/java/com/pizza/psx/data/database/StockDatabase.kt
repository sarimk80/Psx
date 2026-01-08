package com.pizza.psx.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pizza.psx.domain.DAO.PortfolioModelDAO
import com.pizza.psx.domain.model.PortfolioModel


@Database(
    entities = [PortfolioModel::class],
    version = 2,
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