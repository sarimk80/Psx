package com.pizza.psx.domain.model

data class PsxOhlcModel(
    val status: Int,
    val message: String,
    val data: List<List<Double>>  // ← must be List<List<Double>>, not List<CandleData>
)

data class CandleData(
    val timestamp: Long,
    val close: Double,
    val volume: Long,
    val open: Double   // ← was named "close" before, this is actually open
) {
    companion object {
        fun fromRaw(raw: List<Double>) = CandleData(
            timestamp = raw[0].toLong(),
            close = raw[1],
            volume = raw[2].toLong(),
            open = raw[3]
        )
    }
}