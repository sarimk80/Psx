package com.example.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psx.domain.model.PortfolioModel
import com.example.psx.domain.model.StockResult
import com.example.psx.domain.model.Ticker
import com.example.psx.domain.repo.PortfolioRepo
import com.example.psx.domain.usecase.TickerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList


@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val repo:PortfolioRepo,
    private val getTickerDetail: TickerUseCase,
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
                .collect { symbols ->
                    loadTickersForSymbols(symbols)
                }

        }


    }

  private suspend fun loadTickersForSymbols(symbols:  List<PortfolioModel>){

      viewModelScope.launch {
          try {
              val ticketResult = symbols.map { sym ->

                  async { getTickerDetail(type = "REG", symbol = sym.symbol) }
              }.awaitAll()


              val tickerResponse = ticketResult.mapNotNull { result ->
                  if (result is StockResult.Success) result.data else null
              }
              _uiState.value = _uiState.value.copy(
                  isLoading = false,
                  listOfStocks = tickerResponse,
              )
          }catch (e:Exception){
              _uiState.value = _uiState.value.copy(
                  error = "Failed to add to watchlist: ${e.message}"
              )
          }
      }

    }

    fun addToPortfolioModel(symbol:String){
        viewModelScope.launch {
            try {
                val currentList = portfolioModels.value
                val exist = currentList.any { it.symbol == symbol }
                if(exist){
                    repo.deleteSymbol(symbol)
                }else{
                    repo.insertSymbol(PortfolioModel(symbol = symbol))
                }
                delay(30_000)
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    error = "Failed to add to watchlist: ${e.message}"
                )
            }
        }
    }

}


data class PortfolioUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val listOfStocks:List<Ticker>?=null
)