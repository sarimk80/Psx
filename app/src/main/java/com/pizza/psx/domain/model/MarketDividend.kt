package com.pizza.psx.domain.model

data class MarketDividend(
    val Company:String,//": "First Fidelity Leasing Modaraba",
    val Dividend:String,//": "-",
    val Date:String,//": "-",
    val BoardMeeting:String,//": "-",
    val Eps:String,//": "(0.07)",
    val profitLossBeforeTax:String,//": "(1.830)",
    val profitLossAfterTax:String,//": "(1.830)",
    val yearEnded:String,//": "30/09/2025(IQ)"
)
