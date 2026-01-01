package com.pizza.psx.data.network.dot
import com.google.gson.annotations.SerializedName


data class SectorDot(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: Map<String, Datum>,
    @SerializedName("timestamp")
    val timestamp: Long
)


data class Datum (
    @SerializedName("totalVolume")
    val totalVolume: Long,
    @SerializedName("totalValue")
    val totalValue: Double,
    @SerializedName("totalTrades")
    val totalTrades: Long,
    @SerializedName("gainers")
    val gainers: Long,
    @SerializedName("losers")
    val losers: Long,
    @SerializedName("unchanged")
    val unchanged: Long,
    @SerializedName("avgChange")
    val avgChange: Double,
    @SerializedName("avgChangePercent")
    val avgChangePercent: Double,
    @SerializedName("symbols")
    val symbols: List<String>
)
