package com.pizza.psx.domain.model

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson

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

class EtfNavType : NavType<Etf>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): Etf? {
        return bundle.getString(key)?.let {
            Gson().fromJson(it, Etf::class.java)
        }
    }

    override fun parseValue(value: String): Etf {
        return Gson().fromJson(value, Etf::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Etf) {
        bundle.putString(key, Gson().toJson(value))
    }
}