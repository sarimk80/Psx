package com.example.psx.domain.model

data class Sector (
    val success: Boolean,
    val data: Map<String, Datum>,
    val timestamp: Long
)

data class Datum (
    val totalVolume: Long,
    val totalValue: Double,
    val totalTrades: Long,
    val gainers: Long,
    val losers: Long,
    val unchanged: Long,
    val avgChange: Double,
    val avgChangePercent: Double,
    val symbols: List<String>
)
