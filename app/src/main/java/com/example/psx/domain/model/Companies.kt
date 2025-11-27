package com.example.psx.domain.model

data class Companies(
    val success: Boolean,
    val data: CompaniesData,
    val timestamp: Long
)


data class CompaniesData (
    val symbol: String,
    val scrapedAt: String,
    val financialStats: FinancialStats,
    val businessDescription: String,
    val keyPeople: List<KeyPerson>,
    val error: Any? = null
)

data class FinancialStats (
    val marketCap: FreeFloat,
    val shares: FreeFloat,
    val freeFloat: FreeFloat,
    val freeFloatPercent: FreeFloat
)

data class FreeFloat (
    val raw: String,
    val numeric: Double
)

data class KeyPerson (
    val name: String,
    val position: String
)