package com.pizza.psx.domain.usecase

import com.pizza.psx.domain.model.Root
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.repo.StockRepo
import javax.inject.Inject

class GainersUseCase @Inject constructor(
    private  val repo:StockRepo
) {
    suspend operator fun invoke():StockResult<Root>{
        return  repo.getGainerLosers()
    }
}