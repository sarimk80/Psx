package com.example.psx.domain.model

data class Dividend(
    val success: Boolean,
    val data: List<DividendData>,
    val count: Long,
    val symbol: String,
    val timestamp: Long,
    val cacheUpdated: String
)


data class DividendData (
    val symbol: String,
    val exDate: String,
    val paymentDate: String,
    val recordDate: String,
    val amount: Double,
    val year: Long
)