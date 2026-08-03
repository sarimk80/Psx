package com.pizza.psx.domain.model

data class IndexTicker(
    var symbol: String, //": "AIRLINK",
    var name: String, //": "Air Link Communication Limited",
    var ldcp: String, //": "137.07",
    var current: String, //": "134.00",
    var change: Double,//": -3.07,
    var idx_weight: Double,//": 0.65,
    var volume: String,//": "399,811",
    var freeFloat: Double,//": 125,
    var marketCap: Double,//": 16804
)
