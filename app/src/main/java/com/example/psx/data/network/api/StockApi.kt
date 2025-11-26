package com.example.psx.data.network.dot.api

import com.example.psx.data.network.dot.RootDot
import com.example.psx.domain.model.Root
import com.example.psx.domain.model.Sector
import retrofit2.http.GET

interface StockApi {
    //https://psxterminal.com/
    @GET("api/stats/REG")
    suspend fun getGainers(): Root

    @GET("api/stats/sectors")
    suspend fun getSector():Sector
}