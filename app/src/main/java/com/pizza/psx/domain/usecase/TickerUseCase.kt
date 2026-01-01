package com.pizza.psx.domain.usecase

import com.pizza.psx.domain.model.Companies
import com.pizza.psx.domain.model.Dividend
import com.pizza.psx.domain.model.Fundamentals
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.MarketDividend
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.SymbolsModel
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.repo.StockRepo
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

class MarketDividendUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(): StockResult<List<MarketDividend>> {
        return  repo.getMarketDividend()
    }

}

class KLineModelUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(symbol: String,timeFrame:String): StockResult<KLineModel> {
        return  repo.getKLineModel(symbol,timeFrame)
    }

}