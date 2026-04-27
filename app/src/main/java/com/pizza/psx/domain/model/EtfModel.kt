package com.pizza.psx.domain.model

data class EtfModel(
    val etfs: List<Etf>,
)

data class Etf(
    val id: String,
    val etfName: String,
    val fullName: String,
    val description: String,
    val fundSize: String,
    val marketCap: String,
    val noOfShares: String,
    val keyPeople: List<KeyPeople>,
    val symbols: List<Symbol>,
    val type: String,
)

data class KeyPeople(
    val position: String,
    val person: String,
)

data class Symbol(
    val company: String,
    val ticker: String,
    val weight: Double,
)

