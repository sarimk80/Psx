package com.pizza.psx.domain.model

data class KLineModel(
    val success: Boolean,
    val data: List<KLineModelData>,
    val count: Long,
    val symbol: String,
    val timeframe: String,
    val startTimestamp: Any? = null,
    val endTimestamp: Any? = null,
    val timestamp: Long
)

data class KLineModelData (
    val symbol: String,
    val timeframe: String,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)
