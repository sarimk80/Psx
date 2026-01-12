package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.PortfolioModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.repo.PortfolioRepo
import com.pizza.psx.domain.usecase.IndexDetailUseCase
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
):ViewModel(){



    private val _uiState = mutableStateOf(PortfolioUiState())
    val uiState: State<PortfolioUiState> = _uiState

    val portfolioModels: StateFlow<List<PortfolioModel>> =
        repo.getAllSymbols
            .map { items -> items }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getAllPortfolioTicker(){
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            repo.getAllSymbols
                .distinctUntilChanged()
                .first()
                .let { symbols ->
                    loadTickersForSymbols(symbols,emptyList())
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
            loadTickersForSymbols(portfolioViewModelList,emptyList())
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
                        loadTickersForSymbols(symbols = portfolioViewModelList,sectorName)
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

    private suspend fun loadTickersForSymbols(symbols: List<PortfolioModel>, sectorName: List<String>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            if (symbols.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    listOfStocks = emptyList(),
                    isLoading = false
                )
                return@launch
            }

            val symbolToVolumeMap = symbols.associate { it.symbol to it.volume }
            val symbolToSectorMap = if (sectorName.isNotEmpty() && symbols.size == sectorName.size) {
                symbols.mapIndexed { index, model -> model.symbol to sectorName[index] }.toMap()
            } else emptyMap()

            val allTickers = mutableListOf<Ticker>()

            try {
                symbols.chunked(15).forEachIndexed { batchIndex, batch ->
                    val batchTickers = mutableListOf<Ticker>()

                    // Process each symbol sequentially within batch
                    for ((index, sym) in batch.withIndex()) {
                        try {
                            val result = getTickerDetail(type = "REG", symbol = sym.symbol)

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
                            println("Failed to load ${sym.symbol}: ${e.message}")
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

    fun addToPortfolioModel(symbol:String,volume: Int){
        viewModelScope.launch {
            try {
                val currentList = portfolioModels.value
                val exist = currentList.any { it.symbol == symbol }
                if(exist){
                    repo.deleteSymbol(symbol)
                }else{
                    repo.insertSymbol(PortfolioModel(symbol = symbol, volume = volume))
                }
                getAllPortfolioTicker()
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    error = "Failed to add to watchlist: ${e.message}"
                )
            }
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