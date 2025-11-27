package com.example.psx.domain.model

data class SymbolsModel(
    val success: Boolean,
    val data: List<String>,
    val timestamp: Long
)
