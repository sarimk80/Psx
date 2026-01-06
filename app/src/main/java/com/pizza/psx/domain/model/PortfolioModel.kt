package com.pizza.psx.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PortfolioModel(
    @PrimaryKey()
    val symbol: String,
    val volume: Int = 1
)
