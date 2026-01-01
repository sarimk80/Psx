package com.pizza.psx.domain.model

data class Ticker (
    val success: Boolean,
    val data: TickerData,
    val timestamp: Long
)

data class TickerData (
    val market: String,
    val st: String,
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Long,
    val trades: Long,
    val value: Double,
    val high: Double,
    val low: Double,
    val bid: Double,
    val ask: Double,
    val bidVol: Long,
    val askVol: Long,
    val timestamp: Long
)
