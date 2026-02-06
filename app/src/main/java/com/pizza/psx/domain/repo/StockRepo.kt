package com.pizza.psx.domain.repo

import com.pizza.psx.domain.model.Companies
import com.pizza.psx.domain.model.Dividend
import com.pizza.psx.domain.model.Fundamentals
import com.pizza.psx.domain.model.IndexDetailModel
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.MarketDividend
import com.pizza.psx.domain.model.Root
import com.pizza.psx.domain.model.Sector
import com.pizza.psx.domain.model.SectorResponse
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.SymbolDetail
import com.pizza.psx.domain.model.SymbolsModel
import com.pizza.psx.domain.model.Ticker

interface StockRepo {
    suspend fun getGainerLosers(): StockResult<Root>
    suspend fun getSectors():StockResult<Sector>

    suspend fun getTickerDetail(type:String,symbol:String):StockResult<Ticker>

    suspend fun getCompanyDetail(symbol: String):StockResult<Companies>

    suspend fun getCompanyFundamental(symbol: String): StockResult<Fundamentals>

    suspend fun getCompanyDividend(symbol: String):StockResult<Dividend>

    suspend fun getSymbolList():StockResult<SymbolsModel>

    suspend fun getMarketDividend():StockResult<List<MarketDividend>>

    suspend fun getKLineModel(symbol: String,timeFrame:String):StockResult<KLineModel>

    suspend fun getIndexDetail(indexName: String): StockResult<List<IndexDetailModel>>

    suspend fun getSectorResponse(): StockResult<SectorResponse>

    suspend fun getSymbolDetail(symbol: String): StockResult<SymbolDetail>
}