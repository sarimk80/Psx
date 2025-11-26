package com.example.psx.domain.repo

import com.example.psx.domain.model.Root
import com.example.psx.domain.model.Sector
import com.example.psx.domain.model.StockResult

interface StockRepo {
    suspend fun getGainerLosers(): StockResult<Root>
    suspend fun getSectors():StockResult<Sector>
}