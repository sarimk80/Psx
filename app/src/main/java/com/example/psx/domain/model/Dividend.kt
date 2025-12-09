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
    val ex_date: String,
    val payment_date: String,
    val record_date: String,
    val amount: Double,
    val year: Long
)