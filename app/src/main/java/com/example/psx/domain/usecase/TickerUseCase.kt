package com.example.psx.domain.usecase

import com.example.psx.domain.model.Companies
import com.example.psx.domain.model.Dividend
import com.example.psx.domain.model.Fundamentals
import com.example.psx.domain.model.Root
import com.example.psx.domain.model.StockResult
import com.example.psx.domain.model.SymbolsModel
import com.example.psx.domain.model.Ticker
import com.example.psx.domain.repo.StockRepo
import javax.inject.Inject

class TickerUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(type: String, symbol: String): StockResult<Ticker> {
        return  repo.getTickerDetail(type,symbol)
    }

}

class CompanyUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(symbol: String): StockResult<Companies> {
        return  repo.getCompanyDetail(symbol)
    }

}

class CompanyFundamentalUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(symbol: String): StockResult<Fundamentals> {
        return  repo.getCompanyFundamental(symbol)
    }

}

class CompanyDividendUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(symbol: String): StockResult<Dividend> {
        return  repo.getCompanyDividend(symbol)
    }

}

class SymbolListUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(): StockResult<SymbolsModel> {
        return  repo.getSymbolList()
    }

}