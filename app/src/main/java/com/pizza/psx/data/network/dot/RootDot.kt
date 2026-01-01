package com.pizza.psx.data.network.dot
import com.google.gson.annotations.SerializedName




data class RootDot(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: GainersData,
    @SerializedName("timestamp")
    val timestamp: Long,
){
    fun toGainer():RootDot{
        return  RootDot(
            success = success,
            data = data,
            timestamp = timestamp
        )
    }
}

data class GainersData(
    @SerializedName("totalVolume")
    val totalVolume: Long,
    @SerializedName("totalValue")
    val totalValue: Double,
    @SerializedName("totalTrades")
    val totalTrades: Long,
    @SerializedName("symbolCount")
    val symbolCount: Long,
    @SerializedName("gainers")
    val gainers: Long,
    @SerializedName("losers")
    val losers: Long,
    @SerializedName("unchanged")
    val unchanged: Long,
    @SerializedName("topGainers")
    val topGainers: List<TopStocks>,
    @SerializedName("topLosers")
    val topLosers: List<TopStocks>,
){
    fun toGainerData():GainersData{
        return  GainersData(
            totalVolume = totalVolume,
            totalValue = totalValue,
            totalTrades = totalTrades,
            symbolCount = symbolCount,
            gainers = gainers,
            losers = losers,
            unchanged = unchanged,
            topGainers = topGainers,
            topLosers = topLosers
        )
    }
}


data class  TopStocks (
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("change")
    val change: Double,
    @SerializedName("changePercent")
    val changePercent: Double,
    @SerializedName("price")
    val price: Double,
    @SerializedName("volume")
    val volume: Long,
    @SerializedName("value")
    val value: Double,
){
    fun toTopStocks():TopStocks{
        return  TopStocks(
            symbol = symbol,
            change = change,
            changePercent = changePercent,
            price = price,
            volume = volume,
            value = value
        )
    }
}
