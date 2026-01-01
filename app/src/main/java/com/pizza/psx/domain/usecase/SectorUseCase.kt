package com.pizza.psx.domain.usecase

import com.pizza.psx.domain.model.Sector
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.repo.StockRepo
import javax.inject.Inject

class SectorUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(): StockResult<Sector> {
        return  repo.getSectors()
    }
}