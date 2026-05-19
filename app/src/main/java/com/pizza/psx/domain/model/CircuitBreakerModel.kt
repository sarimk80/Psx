package com.pizza.psx.domain.model

data class CircuitBreakerModel(
    val upper: List<TickerBreaker>,
    val lower: List<TickerBreaker>,
)

data class TickerBreaker(
    val symbol: String,
    val ldcp: Double,
    val open: Double,
    val high: Double,
    val low: Double,
    val current: Double,
    val change: Double,
    val change_percent: Double,
    val volume: Long,
)
