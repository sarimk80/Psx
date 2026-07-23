package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.SymbolDetail
import com.pizza.psx.domain.model.SymbolsModel
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.usecase.CompanyDividendUseCase
import com.pizza.psx.domain.usecase.CompanyFundamentalUseCase
import com.pizza.psx.domain.usecase.KLineModelUseCase
import com.pizza.psx.domain.usecase.MarketDividendUseCase
import com.pizza.psx.domain.usecase.SymbolDetailUseCase
import com.pizza.psx.domain.usecase.SymbolListUseCase
import com.pizza.psx.domain.usecase.TickerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CompareStockViewModel@Inject constructor(
    private val symbolListUseCase: SymbolListUseCase,
    private val getTickerDetail: TickerUseCase,
    private val getSymbolDetailUseCase: SymbolDetailUseCase,
)
    : ViewModel() {

    private val _selectedTickers = mutableStateOf<List<String>>(emptyList())
    val selectedTickers: State<List<String>> = _selectedTickers

    private val _tickerState = mutableStateOf<List<TickerSymbolDetail>>(emptyList())
        val tickerUiState: State<List<TickerSymbolDetail>> = _tickerState



    private val _uiState = mutableStateOf(CompareStockSearchUiState())
    val uiState: State<CompareStockSearchUiState> = _uiState

    private val _uiTickerState = mutableStateOf(CompareStockTickerUiState())
    val uiTickerState: State<CompareStockTickerUiState> = _uiTickerState


    fun addRemoveTicker(ticker: String){
        _selectedTickers.value =
            if(_selectedTickers.value.contains(ticker)){
                _selectedTickers.value - ticker
            }else{
                _selectedTickers.value + ticker
            }
    }

    fun removeFilterTicker(ticker: String){
        _selectedTickers.value = _selectedTickers.value - ticker
    }

    fun getSymbolList(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {

                when(val result = symbolListUseCase()){
                    is StockResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            symbolList = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is StockResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    StockResult.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }

            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun getTickerDetail(){
        viewModelScope.launch {
            _uiTickerState.value = _uiTickerState.value.copy(isLoading = true)
            val list = mutableListOf<TickerSymbolDetail>()

            val currentSymbols = _tickerState.value.map { it.ticker?.data?.symbol }

            val symbolsToLoad =
                _selectedTickers.value.filterNot { currentSymbols.contains(it) }

            symbolsToLoad.forEach {
                val tickerDeferred = async {
                    getTickerDetail("REG", it)
                }

                val detailDeferred = async {
                    getSymbolDetailUseCase(it)
                }

                val tickerResult = tickerDeferred.await()
                val detailResult = detailDeferred.await()

                val ticker =
                    (tickerResult as? StockResult.Success)?.data

                val detail =
                    (detailResult as? StockResult.Success)?.data

                if (ticker != null && detail != null) {
                    list.add(
                        TickerSymbolDetail(
                            ticker = ticker,
                            symbolDetail = detail
                        )
                    )
                }
            }

            _tickerState.value = list

            _uiTickerState.value =
                _uiTickerState.value.copy(
                    isLoading = false
                )

            }

        }
    }



data class CompareStockSearchUiState(
    val symbolList: SymbolsModel? = null,
    val searchedSymbol:SymbolsModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class CompareStockTickerUiState(
    val ticker: Ticker? = null,
    val symbolDetail: SymbolDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class TickerSymbolDetail(
    val ticker: Ticker? = null,
    val symbolDetail: SymbolDetail? = null
)