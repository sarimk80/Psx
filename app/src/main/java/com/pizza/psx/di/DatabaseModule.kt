package com.pizza.psx.di

import android.content.Context
import com.pizza.psx.data.database.StockDatabase
import com.pizza.psx.domain.DAO.PortfolioModelDAO
import com.pizza.psx.domain.repo.PortfolioRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStockDatabase(@ApplicationContext context: Context): StockDatabase {
        return StockDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideWatchlistDao(database: StockDatabase) = database.portfolioModelDAO()

    @Provides
    @Singleton
    fun provideWatchlistRepository(dao: PortfolioModelDAO) = PortfolioRepo(dao)
}