package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.IndexDetailModel
import com.pizza.psx.domain.model.IndexPriceModel
import com.pizza.psx.domain.model.IndexTicker
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.PortfolioModel
import com.pizza.psx.domain.model.PsxOhlcModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.usecase.GetAllIndexTickerUseCase
import com.pizza.psx.domain.usecase.IndexDetailUseCase
import com.pizza.psx.domain.usecase.IndexPriceUseCase
import com.pizza.psx.domain.usecase.KLineModelUseCase
import com.pizza.psx.domain.usecase.TickerUseCase
import com.pizza.psx.presentation.helpers.stringToIndexString
import com.pizza.psx.views.FilterOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndexDetailViewModel @Inject constructor(
    private val getTickerDetail: TickerUseCase,
    private val indexDetailUseCase: IndexDetailUseCase,
    private val kLineModel: KLineModelUseCase,
    private val getIndexTickerDetailUiState: GetAllIndexTickerUseCase,
    savedStateHandle: SavedStateHandle
):ViewModel() {

    //private val _uiState = mutableStateOf(IndexDetailUiState())
    private val _indexUiState = mutableStateOf(IndexChartList())

    private val _indexSymbolUiState = mutableStateOf(IndexSymbolUiState())



    //val uiState: State<IndexDetailUiState> = _uiState
    val indexUiState: State<IndexChartList> = _indexUiState

    val indexSymbolState: State<IndexSymbolUiState> = _indexSymbolUiState


   private val indexSymbol: String = checkNotNull(savedStateHandle["indexSymbol"])

    init {
        getChartIndex(stringToIndexString(indexSymbol))
        getIndexSymbols(indexName = indexSymbol)
    }


    fun getChartIndex(indexName: String) {
        viewModelScope.launch {
            _indexUiState.value = _indexUiState.value.copy(
                isLoading = true,
            )

            try {
                val priceDeferred = async { kLineModel(indexSymbol,) }
                val sectorDeferred = async { indexDetailUseCase(indexName) }

                val priceResult = priceDeferred.await()
                val sectorResult = sectorDeferred.await()

                val priceData = when (priceResult) {
                    is StockResult.Success -> priceResult.data
                    is StockResult.Error -> {
                        _indexUiState.value = _indexUiState.value.copy(error = priceResult.message)
                        null
                    }
                    is StockResult.Loading -> null
                }

                val sectorData = when (sectorResult) {
                    is StockResult.Success -> sectorResult.data
                    is StockResult.Error -> {
                        _indexUiState.value = _indexUiState.value.copy(error = sectorResult.message)
                        null
                    }
                    is StockResult.Loading -> null
                }

                _indexUiState.value = _indexUiState.value.copy(
                    isLoading = false,
                    listOfStocks = sectorData,
                    indexPrice = priceData
                )

            } catch (e: Exception) {
                _indexUiState.value = _indexUiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun getIndexSymbols(indexName: String){

        viewModelScope.launch {

            _indexSymbolUiState.value = _indexSymbolUiState.value.copy(isLoading = true)

            try {
                when(val result = getIndexTickerDetailUiState(indexName)){

                    is StockResult.Success -> {
                        _indexSymbolUiState.value = _indexSymbolUiState.value.copy(listOfStocks = result.data, isLoading = false)
                    }

                    is StockResult.Loading -> {
                        _indexSymbolUiState.value = _indexSymbolUiState.value.copy(isLoading = true)
                    }

                    is StockResult.Error -> {
                        _indexSymbolUiState.value = _indexSymbolUiState.value.copy(error = result.message, isLoading = false)
                    }
                }
            }catch (e: Exception){
                _indexSymbolUiState.value = _indexSymbolUiState.value.copy(isLoading = false)
            }
        }
    }


    fun filterTicker(
        listOfStocks: List<IndexTicker>,
        filterOption: FilterOption
    ) {
        val sortedList = when (filterOption) {
            FilterOption.HIGH ->
                listOfStocks.sortedByDescending { it.current.toFloatOrNull() ?: 0f }

            FilterOption.LOW ->
                listOfStocks.sortedBy { it.current.toFloatOrNull() ?: 0f }

            FilterOption.CURRENT ->
                listOfStocks.sortedBy { it.current.toFloatOrNull() ?: 0f }

            FilterOption.INDEX_WEIGHT ->
                listOfStocks.sortedByDescending { it.idx_weight }

            FilterOption.VOLUME ->
                listOfStocks.sortedByDescending {
                    it.volume.replace(",", "").toFloatOrNull() ?: 0f
                }
        }

        _indexSymbolUiState.value = _indexSymbolUiState.value.copy(
            listOfStocks = sortedList,
            isLoading = false,
            filterOption = filterOption
        )
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
    val listOfStocks:List<IndexDetailModel>?=null,
    val indexPrice: PsxOhlcModel?= null
)

data class IndexSymbolUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val listOfStocks:List<IndexTicker>?=null,
    val filterOption: FilterOption = FilterOption.CURRENT
)