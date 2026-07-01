package com.pizza.psx.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson

@Parcelize
data class Ticker (
    val success: Boolean,
    val data: TickerData,
    val timestamp: Long
): Parcelable

@Parcelize
data class TickerData (
    val market: String,
    val st: String,
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Long,
    val trades: Long,
    val value: Double,
    val high: Double,
    val low: Double,
    val bid: Double,
    val ask: Double,
    val bidVol: Long,
    val askVol: Long,
    val timestamp: Long,
    val circuit_breaker:String,
    val day_range:String,
    val week_range_52:String,
    val ldcp: Double,
    val haircut:Double,
    val price_earning: Double,
    val year_1_change:Double,
    val ytd_change:Double,
    var stockCount: Int = 0,
    var sectorName: String = "",
    var market_cap: Double,
    var shares: Double,
    var free_float: Double,
    var free_float_share: Double,
    var key_people:List<KeyPerson>,
    var business_description: String,
    var p_e_ratio: Double,
    var dividend: List<DividendModel>,

): Parcelable

@Parcelize
data class DividendModel (

    val announcement_date: String,//": "2026-04-28T16:05:00",
    val period_end: String,//": "2026-03-31",
    val period: String,//": "Third Quarter",
    val percentage: Double,//": 80,
    val installment: String,//": "Third",
    val type: String,//": "Dividend",
    val book_closure_start: String,//": "2026-05-12",
    val book_closure_end: String,//": "2026-05-14"

):Parcelable

object TickerNavType : NavType<Ticker>(isNullableAllowed = false) {

    private val gson = Gson()

    override fun get(bundle: Bundle, key: String): Ticker? {
        return bundle.getString(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): Ticker {
        return try {
            val decodedValue = java.net.URLDecoder.decode(value, "UTF-8")
            gson.fromJson(decodedValue, Ticker::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException("Could not parse Ticker JSON: $value", e)
        }
    }

    override fun put(bundle: Bundle, key: String, value: Ticker) {
        val jsonString = gson.toJson(value)
        bundle.putString(key, jsonString)
    }
}