package com.example.psx.domain.repo

import com.example.psx.domain.model.Companies
import com.example.psx.domain.model.Dividend
import com.example.psx.domain.model.Fundamentals
import com.example.psx.domain.model.Root
import com.example.psx.domain.model.Sector
import com.example.psx.domain.model.StockResult
import com.example.psx.domain.model.SymbolsModel
import com.example.psx.domain.model.Ticker

interface StockRepo {
    suspend fun getGainerLosers(): StockResult<Root>
    suspend fun getSectors():StockResult<Sector>

    suspend fun getTickerDetail(type:String,symbol:String):StockResult<Ticker>

    suspend fun getCompanyDetail(symbol: String):StockResult<Companies>

    suspend fun getCompanyFundamental(symbol: String): StockResult<Fundamentals>

    suspend fun getCompanyDividend(symbol: String):StockResult<Dividend>

    ///api/symbols
    suspend fun getSymbolList():StockResult<SymbolsModel>
}