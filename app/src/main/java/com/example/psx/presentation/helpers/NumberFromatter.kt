package com.example.psx.presentation.helpers

import java.text.DecimalFormat

fun number_format(amount:Double):String{
    val numberFormat = DecimalFormat("#,###.00")
    return numberFormat.format(amount)
}