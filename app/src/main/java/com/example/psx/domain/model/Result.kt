package com.example.psx.domain.model

sealed class StockResult<out T>{
    data class Success<T>(val data: T) : StockResult<T>()
    data class Error(val message: String) : StockResult<Nothing>()
    object Loading : StockResult<Nothing>()
}

