package com.pizza.psx.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.RoomDatabase
import com.pizza.psx.domain.model.IndexDetailModel
import com.pizza.psx.domain.model.IndexPriceModel
import com.pizza.psx.domain.model.PortfolioModel
import com.pizza.psx.domain.model.PortfolioWithTransactions
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.model.Transaction
import com.pizza.psx.domain.repo.PortfolioRepo
import com.pizza.psx.domain.usecase.IndexDetailUseCase
import com.pizza.psx.domain.usecase.IndexPriceUseCase
import com.pizza.psx.domain.usecase.TickerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration


@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val repo:PortfolioRepo,
    private val getTickerDetail: TickerUseCase,
    private val indexDetailUseCase: IndexDetailUseCase,
    private val indexPrice: IndexPriceUseCase
):ViewModel(){



    private val _uiState = mutableStateOf(PortfolioUiState())
    private val _indexUiState = mutableStateOf(IndexList())

    val uiState: State<PortfolioUiState> = _uiState
    val indexUiState: State<IndexList> = _indexUiState

    val portfolioModels: StateFlow<List<PortfolioModel>> =
        repo.getAllSymbols
            .map { items -> items }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    init {
        viewModelScope.launch {
            val transactions = repo.getAllTransaction()
            Log.d("DB_CHECK", "Transactions on app start: $transactions")

            val portfolios = repo.getAllSymbols.first()
            Log.d("DB_CHECK", "Portfolios on app start: $portfolios")
        }
        startPortfolioPolling()
    }

    private fun startPortfolioPolling() {
        viewModelScope.launch {
            while (true) {
                getAllPortfolioTicker()
                delay(70_000)
            }
        }
    }


    fun getAllPortfolioTicker(){
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {


            repo.getAllSymbolTransaction()
                .first() // This gets the first emission from the Flow
                .let { portfoliosWithTransactions ->
                    loadTickersForSymbols(portfoliosWithTransactions, emptyList())
                }

        }


    }

    fun getSectorTicker(symbols:List<String>){
        _uiState.value = _uiState.value.copy(isLoading = true)
         var portfolioViewModelList = mutableListOf<PortfolioModel>()
       symbols.forEach { symbol ->
           portfolioViewModelList.add(PortfolioModel(
               symbol = symbol,
               volume = 1
           ))
       }
        viewModelScope.launch{
            loadTickersForSymbols(emptyList(),emptyList())
        }
    }

    fun getIndexDetail(indexName:String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            var portfolioViewModelList = mutableListOf<PortfolioModel>()
         try {

                when(val result = indexDetailUseCase(indexName = indexName)){
                    is StockResult.Success -> {
                       result.data.forEach{data ->
                           portfolioViewModelList.add(PortfolioModel(
                               symbol = data.symbol,
                               volume = 1
                           ))
                       }
                        var sectorName = result.data.map{it.sector}
                        loadTickersForSymbols(symbols = emptyList(),sectorName)
                    }
                    is StockResult.Error -> {
                        print(result.message)
                    }
                    StockResult.Loading -> {
                    }
                }

            }catch (e:Exception){
            }
        }
    }

    fun getChartIndex(indexName: String){
        viewModelScope.launch {
            _indexUiState.value = _indexUiState.value.copy(isLoading = true)

            try {

                when(val result = indexPrice(indexName = indexName)){
                    is StockResult.Success -> {
                        _indexUiState.value = _indexUiState.value.copy(listOfStocks = result.data, isLoading = false)
                    }
                    is StockResult.Error -> {
                        _indexUiState.value = _indexUiState.value.copy(error = result.message)
                    }
                    StockResult.Loading -> {
                        _indexUiState.value = _indexUiState.value.copy(isLoading = true)
                    }
                }

            }catch (e:Exception){
                _indexUiState.value = _indexUiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private suspend fun loadTickersForSymbols(symbols:  List<PortfolioWithTransactions>, sectorName: List<String>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            if (symbols.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    listOfStocks = emptyList(),
                    isLoading = false
                )
                return@launch
            }
            val symbolToVolumeMap = symbols.associate { portfolioWithTx ->

                val volume = if (portfolioWithTx.transactions.isEmpty()) {
                    // No transactions → fallback to base portfolio volume
                    portfolioWithTx.portfolio.volume
                } else {
                    portfolioWithTx.transactions.sumOf { tx ->
                        val vol = tx.volume ?: portfolioWithTx.portfolio.volume

                        when (tx.transactionStatus) {
                            "Buy" -> vol
                            "Sell" -> -vol
                            else -> portfolioWithTx.portfolio.volume
                        }
                    }
                }

                portfolioWithTx.portfolio.symbol to volume
            }

            val symbolToSectorMap = if (sectorName.isNotEmpty() && symbols.size == sectorName.size) {
                symbols.mapIndexed { index, model -> model.portfolio.symbol to sectorName[index] }.toMap()
            } else emptyMap()

            val allTickers = mutableListOf<Ticker>()

            try {
                symbols.chunked(15).forEachIndexed { batchIndex, batch ->
                    val batchTickers = mutableListOf<Ticker>()

                    // Process each symbol sequentially within batch
                    for ((index, sym) in batch.withIndex()) {
                        try {
                            val result = getTickerDetail(type = "REG", symbol = sym.portfolio.symbol)

                            if (result is StockResult.Success) {
                                val ticker = result.data
                                ticker.data.stockCount = symbolToVolumeMap[ticker.data.symbol] ?: 0
                                symbolToSectorMap[ticker.data.symbol]?.let { sector ->
                                    ticker.data.sectorName = sector
                                }
                                batchTickers.add(ticker)
                            }

                            // Add delay after each API call (except the last one in batch)
                            if (index < batch.size - 1) {
                                delay(100) // 100ms delay between API calls
                            }

                        } catch (e: Exception) {
                            // Handle individual API call failure
                            println("Failed to load ${sym.portfolio.symbol}: ${e.message}")
                        }
                    }

                    allTickers.addAll(batchTickers)

                    // Update UI after each batch
                    _uiState.value = _uiState.value.copy(
                        listOfStocks = allTickers.toList(),
                        isLoading = false
                    )

                    // Optional: Add delay between batches
                    if (batchIndex < symbols.chunked(15).size - 1) {
                        delay(200) // Additional delay between batches
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    listOfStocks = allTickers,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load stocks: ${e.message}"
                )
            }
        }
    }

    fun addToPortfolioModel(symbol:String,volume: Int,isFromEditVolume: Boolean = false,transaction: Transaction){
        viewModelScope.launch {
            try {
                val currentList = portfolioModels.value
                val exist = currentList.any { it.symbol == symbol }
                if(exist){
                    repo.updateSymbol(PortfolioModel(symbol,volume))
                    //repo.insertTransaction(transaction)
                    try {
                        repo.insertTransaction(transaction)
                        Log.d("DB_CHECK", "Transaction inserted successfully")
                    } catch (e: Exception) {
                        Log.e("DB_CHECK", "Insert failed", e) // SQLiteConstraintException? FK violation?
                    }

                }else{
                    repo.insertSymbol(PortfolioModel(symbol = symbol, volume = volume))
                    try {
                        repo.insertTransaction(transaction)
                        Log.d("DB_CHECK", "Transaction inserted successfully")
                    } catch (e: Exception) {
                        Log.e("DB_CHECK", "Insert failed", e) // SQLiteConstraintException? FK violation?
                    }


                }
                getAllPortfolioTicker()
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    error = "Failed to add to watchlist: ${e.message}"
                )
            }
        }
    }

    fun removeFromWatchlist(symbol: String) {
        viewModelScope.launch {
            try {
                repo.deleteSymbol(symbol)
            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete: ${e.message}"
                )
            }
            getAllPortfolioTicker()
        }
    }

    fun checkIfSymbolExist(symbol: String): Boolean {
        val currentList = portfolioModels.value
        return currentList.any { it.symbol == symbol }
    }


}


data class PortfolioUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val listOfStocks:List<Ticker>?=null
)
//List<IndexDetailModel>
data class IndexList(
    val isLoading: Boolean = true,
    val error: String? = null,
    val listOfStocks:IndexPriceModel?=null
)