package com.pizza.psx.domain.usecase

import com.pizza.psx.domain.model.CircuitBreakerModel
import com.pizza.psx.domain.model.Companies
import com.pizza.psx.domain.model.CurrencyExchangeModel
import com.pizza.psx.domain.model.Dividend
import com.pizza.psx.domain.model.EtfModel
import com.pizza.psx.domain.model.Fundamentals
import com.pizza.psx.domain.model.IndexDetailModel
import com.pizza.psx.domain.model.IndexPriceModel
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.MarketDividend
import com.pizza.psx.domain.model.MetalsModel
import com.pizza.psx.domain.model.PsxOhlcModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.SymbolDetail
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
    suspend operator fun invoke(symbol: String): StockResult<PsxOhlcModel> {
        return  repo.getKLineModel(symbol)
    }

}

class IndexDetailUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(indexName: String): StockResult<List<IndexDetailModel>> {
        return  repo.getIndexDetail(indexName = indexName)
    }

}

class SymbolDetailUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(symbol: String): StockResult<SymbolDetail> {
        return  repo.getSymbolDetail(symbol)
    }

}

class IndexPriceUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(indexName: String): StockResult<IndexPriceModel> {
        return  repo.getIndexPrice(indexName = indexName)
    }

}

class EtfUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(): StockResult<EtfModel> {
        return repo.getAllEtf()
    }
}

class AllIndicesUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(): StockResult<List<Ticker>> {
        return repo.getAllIndices()
    }
}

class AllCircuitBreakerUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(): StockResult<CircuitBreakerModel> {
        return repo.getAllCicuirBreaker()
    }
}

class CurrencyExchangeUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(): StockResult<CurrencyExchangeModel> {
        return repo.getAllCurrencyExchange()
    }
}

class CacheTickerUseCase@Inject constructor(
    private  val repo: StockRepo
){
    suspend operator fun invoke(): StockResult<List<Ticker>> {
        return repo.cacheTickerList()
    }
}

class MetalsUseCase@Inject constructor(
    private  val repo: StockRepo
){
    suspend operator fun invoke(metal:String): StockResult<List<MetalsModel>> {
        return repo.getAllMetals(metal)
    }
}