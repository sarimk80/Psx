package com.pizza.psx.domain.model

data class IndexDetailModel(
    val name: String,
    val sector: String,
    val symbol: String,

    )

data class SectorName(
    val sectorName: String,
    val sectorCount: Int,
)