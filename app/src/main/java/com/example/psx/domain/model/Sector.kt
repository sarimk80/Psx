package com.example.psx.domain.model

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sector (
    val success: Boolean,
    val data: Map<String, Datum>,
    val timestamp: Long
): Parcelable

@Parcelize
data class Datum (
    val totalVolume: Long,
    val totalValue: Double,
    val totalTrades: Long,
    val gainers: Long,
    val losers: Long,
    val unchanged: Long,
    val avgChange: Double,
    val avgChangePercent: Double,
    val symbols: List<String>
): Parcelable


object DatumNavType : NavType<Datum>(isNullableAllowed = false) {
    private val gson = Gson()

    override fun get(bundle: Bundle, key: String): Datum? {
        return bundle.getString(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): Datum {
        return try {
            // Decode URL encoding first if needed
            val decodedValue = java.net.URLDecoder.decode(value, "UTF-8")
            gson.fromJson(decodedValue, Datum::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException("Could not parse Datum JSON: $value", e)
        }
    }

    override fun put(bundle: Bundle, key: String, value: Datum) {
        val jsonString = gson.toJson(value)
        bundle.putString(key, jsonString)
    }
}
