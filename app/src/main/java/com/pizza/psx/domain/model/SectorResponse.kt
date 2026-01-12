package com.pizza.psx.domain.model

import kotlinx.serialization.SerialName

data class SectorResponse(
    val sectors: Map<String, List<StockData>>
)

data class StockData(
    val script_code: String = "",

    val script_name: String,

    val ldcp: String,
    val open: String,
    val high: String,
    val low: String,
    val current: String,
    val change: String,
    val volume: String,
    val trend: String
)
