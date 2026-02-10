package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.IndexDetailModel
import com.pizza.psx.domain.model.PortfolioModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.usecase.IndexDetailUseCase
import com.pizza.psx.domain.usecase.TickerUseCase
import com.pizza.psx.presentation.helpers.stringToIndexString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndexDetailViewModel @Inject constructor(
    private val getTickerDetail: TickerUseCase,
    private val indexDetailUseCase: IndexDetailUseCase,
    savedStateHandle: SavedStateHandle
):ViewModel() {

    private val _uiState = mutableStateOf(IndexDetailUiState())
    private val _indexUiState = mutableStateOf(IndexChartList())

    val uiState: State<IndexDetailUiState> = _uiState
    val indexUiState: State<IndexChartList> = _indexUiState

   private val indexSymbol: String = checkNotNull(savedStateHandle["indexSymbol"])

    init {
        getIndexDetail(stringToIndexString(indexSymbol))
        getChartIndex(stringToIndexString(indexSymbol))
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

    fun getChartIndex(indexName: String){
        viewModelScope.launch {
            _indexUiState.value = _indexUiState.value.copy(isLoading = true)

            try {

                when(val result = indexDetailUseCase(indexName = indexName)){
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
}

data class IndexDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val listOfStocks:List<Ticker>?=null
)
//List<IndexDetailModel>
data class IndexChartList(
    val isLoading: Boolean = true,
    val error: String? = null,
    val listOfStocks:List<IndexDetailModel>?=null
)