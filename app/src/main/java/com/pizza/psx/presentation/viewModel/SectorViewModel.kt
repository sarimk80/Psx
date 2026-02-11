package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.PortfolioModel
import com.pizza.psx.domain.model.Sector
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.usecase.SectorUseCase
import com.pizza.psx.domain.usecase.TickerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SectorViewModel @Inject constructor(
    private val getSector: SectorUseCase,
    private val getTickerDetail: TickerUseCase,
    ) : ViewModel(){
    private val _uiState = mutableStateOf(SectorUiState())
    val uiState: State<SectorUiState> = _uiState

    private val _sectorDetailUiState = mutableStateOf(SectorDetailUiState())
    val sectorDetailUiState: State<SectorDetailUiState> = _sectorDetailUiState


    fun getSectorAll(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val answer = getSector()){
                is StockResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        stocks = answer.data,
                        isLoading = false,
                        error = null
                    )
                }
                is StockResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = answer.message
                    )
                }
                StockResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun getSectorTicker(symbols:List<String>){
        _sectorDetailUiState.value = _sectorDetailUiState.value.copy(isLoading = true)
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

    private suspend fun loadTickersForSymbols(symbols: List<PortfolioModel>, sectorName: List<String>) {
        viewModelScope.launch {
            _sectorDetailUiState.value = _sectorDetailUiState.value.copy(isLoading = true)

            if (symbols.isEmpty()) {
                _sectorDetailUiState.value = _sectorDetailUiState.value.copy(
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
                    _sectorDetailUiState.value = _sectorDetailUiState.value.copy(
                        listOfStocks = allTickers.toList(),
                        isLoading = false
                    )

                    // Optional: Add delay between batches
                    if (batchIndex < symbols.chunked(15).size - 1) {
                        delay(200) // Additional delay between batches
                    }
                }

                _sectorDetailUiState.value = _sectorDetailUiState.value.copy(
                    isLoading = false,
                    listOfStocks = allTickers,
                    error = null
                )

            } catch (e: Exception) {
                _sectorDetailUiState.value = _sectorDetailUiState.value.copy(
                    isLoading = false,
                    error = "Failed to load stocks: ${e.message}"
                )
            }
        }
    }
}


data class SectorUiState(
    val stocks: Sector? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class SectorDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val listOfStocks:List<Ticker>?=null
)