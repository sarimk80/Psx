package com.pizza.psx.domain.model

data class IndexPriceModel(
    val status: Int,
    val message: String,
    val data: List<IndexData>,
)

data class IndexData(
    val date: Long,
    val price: Double,
    val volume: Long,
)