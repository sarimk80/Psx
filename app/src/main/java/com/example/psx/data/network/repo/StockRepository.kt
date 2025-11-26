package com.example.psx.data.network.repo

import com.example.psx.data.network.dot.api.StockApi
import com.example.psx.domain.model.Root
import com.example.psx.domain.model.Sector
import com.example.psx.domain.model.StockResult
import com.example.psx.domain.repo.StockRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockRepository(
    private val stockApi: StockApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
):StockRepo {
    override suspend fun getGainerLosers(): StockResult<Root>  = withContext(dispatcher){
        return@withContext try {
            val result = stockApi.getGainers()
            StockResult.Success(result)
        }catch (e:Exception){
            StockResult.Error("Failed${e.toString()}")
        }
    }

    override suspend fun getSectors(): StockResult<Sector> = withContext(dispatcher){
        return@withContext try {
            val result = stockApi.getSector()
            StockResult.Success(result)
        }catch (e:Exception){
            StockResult.Error("Failed${e.toString()}")
        }
    }


}