package com.example.psx.data.network.repo

import com.example.psx.data.network.dot.api.StockApi
import com.example.psx.domain.model.Companies
import com.example.psx.domain.model.Dividend
import com.example.psx.domain.model.Fundamentals
import com.example.psx.domain.model.KLineModel
import com.example.psx.domain.model.MarketDividend
import com.example.psx.domain.model.Root
import com.example.psx.domain.model.Sector
import com.example.psx.domain.model.StockResult
import com.example.psx.domain.model.SymbolsModel
import com.example.psx.domain.model.Ticker
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

    override suspend fun getTickerDetail(type:String,symbol:String): StockResult<Ticker> = withContext(dispatcher){
        return@withContext try {
            val result = stockApi.getTickerDetail(type = type, symbol = symbol)
            StockResult.Success(result)

        }catch (e:Exception){
            StockResult.Error("Failed${e.toString()}")
        }
    }

    override suspend fun getCompanyDetail(symbol: String): StockResult<Companies> = withContext(dispatcher) {
        return@withContext try {
            val result = stockApi.getCompanyDetail(symbol)
            StockResult.Success(result)
        }catch (e:Exception){
            StockResult.Error("Failed${e.toString()}")
        }
    }

    override suspend fun getCompanyFundamental(symbol: String): StockResult<Fundamentals> = withContext(dispatcher) {
        return@withContext try {
            val result = stockApi.getCompanyFundamental(symbol)
            StockResult.Success(result)
        }catch (e:Exception){
            StockResult.Error("Failed${e.toString()}")
        }
    }

    override suspend fun getCompanyDividend(symbol: String): StockResult<Dividend>  = withContext(dispatcher){
        return@withContext try {
            val result = stockApi.getCompanyDividend(symbol)
            StockResult.Success(result)
        }catch (e:Exception){
            StockResult.Error("Failed${e.toString()}")
        }
    }

    override suspend fun getSymbolList(): StockResult<SymbolsModel> = withContext(dispatcher){
        return@withContext try {
            val result = stockApi.getSymbolList()
            StockResult.Success(result)
        }catch (e:Exception){
            StockResult.Error("Failed${e.toString()}")
        }
    }



    override suspend fun getMarketDividend(): StockResult<List<MarketDividend>> = withContext(dispatcher){
        return@withContext try {
            val result = stockApi.getMarketData("https://sarim-pix.hf.space/dividend_history")
            StockResult.Success(result)
        }catch (e:Exception){
            StockResult.Error("Failed${e.toString()}")
        }
    }


    override suspend fun getKLineModel(symbol:String,timeFrame:String): StockResult<KLineModel> = withContext(dispatcher){
        return@withContext try {
            val result = stockApi.getKLineModel(symbol,timeFrame)
            StockResult.Success(result)
        }catch (e:Exception){
            StockResult.Error("Failed${e.toString()}")
        }
    }


}