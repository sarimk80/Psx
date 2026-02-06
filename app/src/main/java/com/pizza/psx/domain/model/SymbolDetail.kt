package com.pizza.psx.domain.model

data class SymbolDetail(
    val announcements: List<Announcement>,
    val financials: Financials,
    val ratios: List<Ratio>,
    val timestamp: String,
)

data class Announcement(
    val date: String,
    val title: String,
    val documentLink: String,
    val pdfLink: String,
)

data class Financials(
    val annual: List<Annual>,
    val quarterly: List<Annual>,
)

data class Annual(
    val period: String,
    val sales: Long?,
    val profitAfterTax: Long?,
    val eps: Double?,
)

data class Ratio(
    val period: String,
    val grossProfitMargin: Double?,
    val netProfitMargin: Double?,
    val epsGrowth: Double?,
    val peg: Double?,
)


