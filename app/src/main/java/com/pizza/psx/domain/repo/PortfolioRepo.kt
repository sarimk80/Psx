package com.pizza.psx.domain.repo

import com.pizza.psx.domain.DAO.PortfolioModelDAO
import com.pizza.psx.domain.model.PortfolioModel
import com.pizza.psx.domain.model.PortfolioWithTransactions
import com.pizza.psx.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PortfolioRepo @Inject constructor(
    private val portfolioModelDAO: PortfolioModelDAO
) {
    val getAllSymbols: Flow<List<PortfolioModel>> = portfolioModelDAO.getAllData()

    suspend fun insertSymbol(model:PortfolioModel) =
        portfolioModelDAO.insertPortfolio(model)

    suspend fun updateSymbol(model: PortfolioModel) =
        portfolioModelDAO.upsertPortfolio(model)

    suspend fun deleteSymbol(symbol: String)=
        portfolioModelDAO.deleteModel(symbol)

    // Add transaction

    suspend fun insertTransaction(transaction: Transaction) =
        portfolioModelDAO.insertPortfolioWithTransaction(transaction)

     fun getSymbolAllTransaction(symbol: String) =
        portfolioModelDAO.getPortfolioWithTransactions(symbol)

     fun getAllSymbolTransaction():Flow<List<PortfolioWithTransactions>> =
        portfolioModelDAO.getAllPortfoliosWithTransactions()

    suspend fun getAllTransaction(): List<com.pizza.psx.domain.model.Transaction> =
        portfolioModelDAO.getAllTransactions()

    suspend fun clearAllData() {
        portfolioModelDAO.deleteAllPortfolios()
        portfolioModelDAO.deleteAllTransactions()
    }

}