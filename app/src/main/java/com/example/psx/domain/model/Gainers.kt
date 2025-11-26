package com.example.psx.domain.model


data class Root(
    val success: Boolean,
    val data: GainersData,
    val timestamp: Long,
)

data class GainersData(
    val totalVolume: Long,
    val totalValue: Double,
    val totalTrades: Long,
    val symbolCount: Long,
    val gainers: Long,
    val losers: Long,
    val unchanged: Long,
    val topGainers: List<TopStocks>,
    val topLosers: List<TopStocks>,
)

data class  TopStocks (
    val symbol: String,
    val change: Double,
    val changePercent: Double,
    val price: Double,
    val volume: Long,
    val value: Double,
)
