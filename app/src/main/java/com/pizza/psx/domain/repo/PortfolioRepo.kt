package com.pizza.psx.domain.repo

import com.pizza.psx.domain.DAO.PortfolioModelDAO
import com.pizza.psx.domain.model.PortfolioModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PortfolioRepo @Inject constructor(
    private val portfolioModelDAO: PortfolioModelDAO
) {
    val getAllSymbols: Flow<List<PortfolioModel>> = portfolioModelDAO.getAllData()

    suspend fun insertSymbol(model:PortfolioModel) =
        portfolioModelDAO.insertPortfolio(model)

    suspend fun deleteSymbol(symbol: String)=
        portfolioModelDAO.deleteModel(symbol)
}