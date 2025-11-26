package com.example.psx.domain.usecase

import com.example.psx.domain.model.Root
import com.example.psx.domain.model.StockResult
import com.example.psx.domain.repo.StockRepo
import javax.inject.Inject

class GainersUseCase @Inject constructor(
    private  val repo:StockRepo
) {
    suspend operator fun invoke():StockResult<Root>{
        return  repo.getGainerLosers()
    }
}