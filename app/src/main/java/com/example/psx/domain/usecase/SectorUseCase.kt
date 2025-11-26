package com.example.psx.domain.usecase

import com.example.psx.domain.model.Sector
import com.example.psx.domain.model.StockResult
import com.example.psx.domain.repo.StockRepo
import javax.inject.Inject

class SectorUseCase@Inject constructor(
    private  val repo: StockRepo
) {
    suspend operator fun invoke(): StockResult<Sector> {
        return  repo.getSectors()
    }
}