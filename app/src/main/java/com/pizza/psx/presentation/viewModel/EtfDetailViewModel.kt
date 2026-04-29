package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pizza.psx.domain.model.Companies
import com.pizza.psx.domain.model.Etf
import com.pizza.psx.domain.model.EtfModel
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.usecase.CompanyUseCase
import com.pizza.psx.domain.usecase.EtfUseCase
import com.pizza.psx.domain.usecase.KLineModelUseCase
import com.pizza.psx.domain.usecase.TickerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EtfDetailViewModel @Inject constructor(
    private val companyUseCase: CompanyUseCase,
    private val kLineModelUseCase: KLineModelUseCase,
    private val tickerUseCase: TickerUseCase,
    private val etfUseCase: EtfUseCase,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val symbol: String = checkNotNull(savedStateHandle["symbol"])
//    private val etfModel: Etf = run {
//        val json = checkNotNull(savedStateHandle.get<String>("etf_model"))
//        Gson().fromJson(json, Etf::class.java)
//    }

    private val _uiState = mutableStateOf(EtfDetailUiState())
    val uiState: State<EtfDetailUiState> = _uiState

    fun getAllEtfData(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val companyDetail = async { companyUseCase(symbol) }
                val kLine = async { kLineModelUseCase(symbol,"1d") }
                val ticker = async { tickerUseCase("REG",symbol) }
                val etfDetail = async { etfUseCase() }

                val results = awaitAll(kLine, companyDetail,ticker,etfDetail)

                val kLineResult = results[0] as StockResult<KLineModel>
                val companyResult = results[1] as StockResult<Companies>
                val tickerResult = results[2] as StockResult<Ticker>
                val etfResult = results[3] as StockResult<EtfModel>

                val company = when(companyResult){
                    is StockResult.Success -> companyResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = companyResult.message)
                    }
                }

                val resultKLine = when(kLineResult){
                    is StockResult.Success -> kLineResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = kLineResult.message)
                    }
                }

                val resultTicker = when(tickerResult){
                    is StockResult.Success -> tickerResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = tickerResult.message)
                    }
                }
                val resultEtf = when(etfResult){
                    is StockResult.Success -> etfResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = etfResult.message)
                    }
                }

                val response = resultEtf as EtfModel
                val etfModel = response.etfs.find { it.etfName == symbol }


                _uiState.value = _uiState.value.copy(
                    etfModel = etfModel,
                    kLine = resultKLine as KLineModel,
                    company = company as Companies,
                    ticker = resultTicker as Ticker,
                    isLoading = false
                    )

            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.toString())
            }
        }
    }
}


data class EtfDetailUiState(
    val etfModel: Etf? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val kLine:KLineModel? = null,
    val company:Companies? = null,
    val ticker:Ticker? = null,
)