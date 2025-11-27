package com.example.psx.data.network.dot.api

import com.example.psx.data.network.dot.RootDot
import com.example.psx.domain.model.Companies
import com.example.psx.domain.model.Dividend
import com.example.psx.domain.model.Fundamentals
import com.example.psx.domain.model.Root
import com.example.psx.domain.model.Sector
import com.example.psx.domain.model.SymbolsModel
import com.example.psx.domain.model.Ticker
import retrofit2.http.GET
import retrofit2.http.Path

interface StockApi {
    //https://psxterminal.com/
    @GET("api/stats/REG")
    suspend fun getGainers(): Root

    @GET("api/stats/sectors")
    suspend fun getSector():Sector

    @GET("api/ticks/{type}/{symbol}")
    suspend fun getTickerDetail(@Path("type") type:String, @Path("symbol") symbol:String):Ticker

    @GET("/api/companies/{symbol}")
    suspend fun getCompanyDetail(@Path("symbol") symbol:String):Companies

    @GET("/api/fundamentals/{symbol}")
    suspend fun getCompanyFundamental(@Path("symbol") symbol:String):Fundamentals

    @GET("/api/dividends/{symbol}")
    suspend fun getCompanyDividend(@Path("symbol") symbol:String):Dividend

    @GET("/api/symbols")
    suspend fun getSymbolList():SymbolsModel
}