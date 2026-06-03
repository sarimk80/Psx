package com.pizza.psx.domain.model

data class CurrencyExchangeModel(
    val response: List<CurrencyResponse>,
)

data class CurrencyResponse(
    val country: String,
    val currency: Double,
    val currencyName: String,
    val type: String
)