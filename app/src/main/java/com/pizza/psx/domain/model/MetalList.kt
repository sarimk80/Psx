package com.pizza.psx.domain.model
import com.google.gson.annotations.SerializedName

data class MetalList(
    @SerializedName("Gold") val gold: String,
    @SerializedName("Silver") val silver: String,
    @SerializedName("Platinum") val platinum: String,
    @SerializedName("Palladium") val palladium: String,
    @SerializedName("WTI Crude Oil") val wtiCrudeOil: String,
    @SerializedName("Brent Crude Oil") val brentCrudeOil: String,
    @SerializedName("Natural Gas") val naturalGas: String,
    @SerializedName("Gasoline") val gasoline: String,
    @SerializedName("Heating Oil") val heatingOil: String,
    @SerializedName("Copper") val copper: String,
    @SerializedName("Corn") val corn: String,
    @SerializedName("Wheat") val wheat: String,
    @SerializedName("Soybeans") val soybeans: String,
    @SerializedName("Oats") val oats: String,
    @SerializedName("Rough Rice") val roughRice: String,
    @SerializedName("Coffee") val coffee: String,
    @SerializedName("Sugar") val sugar: String,
    @SerializedName("Cocoa") val cocoa: String,
    @SerializedName("Cotton") val cotton: String,
    @SerializedName("Lumber") val lumber: String,
    @SerializedName("Orange Juice") val orangeJuice: String,
    @SerializedName("Live Cattle") val liveCattle: String,
    @SerializedName("Feeder Cattle") val feederCattle: String,
    @SerializedName("Lean Hogs") val leanHogs: String
)
