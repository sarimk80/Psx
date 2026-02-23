package com.pizza.psx.domain.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.pizza.psx.domain.model.PortfolioModel
import com.pizza.psx.domain.model.PortfolioWithTransactions
import kotlinx.coroutines.flow.Flow


@Dao
interface PortfolioModelDAO {

    // Only Symbol
    @Query("SELECT * FROM portfoliomodel")
    fun getAllData(): Flow<List<PortfolioModel>>

    @Insert()
    suspend fun insertPortfolio(portfolioModel: PortfolioModel)

    @Upsert
    suspend fun upsertPortfolio(portfolioModel: PortfolioModel)

    @Query("DELETE FROM portfoliomodel WHERE symbol = :symbol")
    suspend fun deleteModel(symbol: String)

    // Transaction
    @Insert()
    suspend fun insertPortfolioWithTransaction(transaction: com.pizza.psx.domain.model.Transaction)

//    @Delete
//    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM `transactions` WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)



    // Relation with symbol and transaction
    @Transaction
    @Query("SELECT * FROM PortfolioModel WHERE symbol = :symbol")
     fun getPortfolioWithTransactions(symbol: String): Flow<List<PortfolioWithTransactions>>

    @Transaction
    @Query("SELECT * FROM PortfolioModel")
     fun getAllPortfoliosWithTransactions(): Flow<List<PortfolioWithTransactions>>

}