package com.pizza.psx.domain.model

data class Fundamentals (
    val success: Boolean,
    val data: FundamentalData,
    val timestamp: Long
)

data class FundamentalData (
    val symbol: String,
    val sector: String,
    val listedIn: String,
    val marketCap: String,
    val price: Double,
    val changePercent: Double,
    val yearChange: Double,
    val peRatio: Double,
    val dividendYield: Double,
    val freeFloat: String,
    val volume30Avg: Double,
    val isNonCompliant: Boolean,
    val timestamp: String
)