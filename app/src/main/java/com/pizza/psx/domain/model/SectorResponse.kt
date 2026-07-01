package com.pizza.psx.domain.model

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class SectorResponse(
    val sectors: Map<String, List<StockData>>
) : Parcelable

@Parcelize
data class StockData(
    val script_code: String = "",
    val script_name: String,
    val ldcp: String,
    val open: String,
    val high: String,
    val low: String,
    val current: String,
    val change: String,
    val volume: String,
    val trend: String
) : Parcelable

object StockDataListNavType : NavType<List<StockData>>(false) {

    private val gson = Gson()

    override fun get(bundle: Bundle, key: String): List<StockData>? {
        return bundle.getString(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): List<StockData> {
        val decoded = java.net.URLDecoder.decode(value, "UTF-8")
        val type = object : com.google.gson.reflect.TypeToken<List<StockData>>() {}.type
        return gson.fromJson(decoded, type)
    }

    override fun put(bundle: Bundle, key: String, value: List<StockData>) {
        bundle.putString(key, gson.toJson(value))
    }
}