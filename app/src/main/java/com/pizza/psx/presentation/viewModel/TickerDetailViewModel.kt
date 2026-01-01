package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.Companies
import com.pizza.psx.domain.model.Dividend
import com.pizza.psx.domain.model.Fundamentals
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.MarketDividend
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.usecase.CompanyDividendUseCase
import com.pizza.psx.domain.usecase.CompanyFundamentalUseCase
import com.pizza.psx.domain.usecase.CompanyUseCase
import com.pizza.psx.domain.usecase.KLineModelUseCase
import com.pizza.psx.domain.usecase.MarketDividendUseCase
import com.pizza.psx.domain.usecase.TickerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TickerDetailViewModel@Inject constructor(
    private val getTickerDetail: TickerUseCase,
    private val getCompanyDetail:CompanyUseCase,
    private val getCompanyFundamental:CompanyFundamentalUseCase,
    private val getCompanyDividend: CompanyDividendUseCase,
    private val marketDividendUseCase: MarketDividendUseCase,
    private val getKLineModelUseCase: KLineModelUseCase

): ViewModel() {

    private val _uiState = mutableStateOf(TickerDetailUiState())
    val uiState: State<TickerDetailUiState> = _uiState

    private val _uiKlineState = mutableStateOf(KLineUiState())
    val uiKlineState: State<KLineUiState> = _uiKlineState

    fun getTickerAndCompanyDetail(type:String,symbol:String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val tickerDetail = async { getTickerDetail(type,symbol) }
                val companyDetail = async { getCompanyDetail(symbol) }
                val companyFundamental = async { getCompanyFundamental(symbol) }
                val companyDividend = async { getCompanyDividend(symbol) }
                val kLine = async { getKLineModelUseCase(symbol,"1d") }

                val (tickerResult,companyResult,fundamentalResult,dividendResult,kLineResult) =
                    awaitAll(tickerDetail,companyDetail,companyFundamental,companyDividend,kLine)

                val ticker = when(tickerResult){
                    is StockResult.Success -> tickerResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = tickerResult.message)
                    }
                }

                val company = when(companyResult){
                    is StockResult.Success -> companyResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = companyResult.message)
                    }
                }

                val fundamental = when(fundamentalResult){
                    is StockResult.Success -> fundamentalResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = fundamentalResult.message)
                    }
                }

                val dividend = when(dividendResult){
                    is StockResult.Success -> dividendResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = dividendResult.message)
                    }
                }

                val resultKLine = when(kLineResult){
                    is StockResult.Success -> kLineResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = kLineResult.message)
                    }
                }



                _uiState.value = _uiState.value.copy(
                    stocks = ticker as Ticker,
                    company = company as Companies,
                    fundamentals=fundamental as Fundamentals,
                    dividend = dividend as Dividend,
                    kLine = resultKLine as KLineModel,
                    isLoading = false,
                    error = null
                )

            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

   fun getTickerDetailAll(type:String,symbol:List<String>){
       viewModelScope.launch {
           _uiState.value = _uiState.value.copy(isLoading = true)
           try {

               val ticketResult = symbol.map { sym ->

                   async { getTickerDetail(type = "IDX", symbol = sym) }
               }.awaitAll()


            val tickerResponse = ticketResult.mapNotNull { result ->
                if (result is StockResult.Success) result.data else null
            }

               _uiState.value = _uiState.value.copy(
                   listOfTicker = tickerResponse,
                   isLoading = false,
                   error = null
               )

           }catch (e:Exception){
               _uiState.value = _uiState.value.copy(
                   isLoading = false,
                   error = e.message
               )
           }

       }
   }

    fun getCompanyDetailAll(type:String,symbol:String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val answer = getCompanyDetail(symbol)){
                is StockResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        company = answer.data,
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
                is  StockResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun getMarketDividend() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDividendLoading = true, error = null)
            when (val result = marketDividendUseCase()) {
                is StockResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        marketDividend = result.data,
                        isDividendLoading = false,
                        error = null
                    )
                }
                is StockResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isDividendLoading = false,
                        error = result.message
                    )
                }
                is StockResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isDividendLoading = true)
                }
            }
        }
    }


    fun getKlineData(symbol: String, interval: String = "1h") {
        viewModelScope.launch {
            _uiKlineState.value = _uiKlineState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val result = getKLineModelUseCase(symbol, interval)

                when (result) {
                    is StockResult.Success -> {
                        _uiKlineState.value = _uiKlineState.value.copy(
                            kLine = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is StockResult.Error -> {
                        _uiKlineState.value = _uiKlineState.value.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load kline data"
                        )
                    }
                    is StockResult.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                _uiKlineState.value = _uiKlineState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message}"
                )
            }
        }
    }


}


data class TickerDetailUiState(
    val stocks: Ticker? = null,
    val company:Companies? = null,
    val fundamentals: Fundamentals?=null,
    val listOfTicker:List<Ticker>? = null,
    val dividend: Dividend? = null,
    val marketDividend:List<MarketDividend>? = null,
    val kLine:KLineModel? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isDividendLoading:Boolean = true,
)


data class KLineUiState(
    val isLoading:Boolean = true,
    val error: String? = null,
    val kLine:KLineModel? = null,

)
