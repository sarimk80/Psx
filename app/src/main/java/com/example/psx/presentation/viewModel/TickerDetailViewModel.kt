package com.example.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psx.domain.model.Companies
import com.example.psx.domain.model.Dividend
import com.example.psx.domain.model.Fundamentals
import com.example.psx.domain.model.Root
import com.example.psx.domain.model.StockResult
import com.example.psx.domain.model.Ticker
import com.example.psx.domain.usecase.CompanyDividendUseCase
import com.example.psx.domain.usecase.CompanyFundamentalUseCase
import com.example.psx.domain.usecase.CompanyUseCase
import com.example.psx.domain.usecase.GainersUseCase
import com.example.psx.domain.usecase.TickerUseCase
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
    private val getCompanyDividend: CompanyDividendUseCase
): ViewModel() {

    private val _uiState = mutableStateOf(TickerDetailUiState())
    val uiState: State<TickerDetailUiState> = _uiState

    fun getTickerAndCompanyDetail(type:String,symbol:String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val tickerDetail = async { getTickerDetail(type,symbol) }
                val companyDetail = async { getCompanyDetail(symbol) }
                val companyFundamental = async { getCompanyFundamental(symbol) }
                val companyDividend = async { getCompanyDividend(symbol) }

                val (tickerResult,companyResult,fundamentalResult,dividendResult) = awaitAll(tickerDetail,companyDetail,companyFundamental,companyDividend)

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

                _uiState.value = _uiState.value.copy(
                    stocks = ticker as Ticker,
                    company = company as Companies,
                    fundamentals=fundamental as Fundamentals,
                    dividend = dividend as Dividend,
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
}


data class TickerDetailUiState(
    val stocks: Ticker? = null,
    val company:Companies? = null,
    val fundamentals: Fundamentals?=null,
    val listOfTicker:List<Ticker>? = null,
    val dividend: Dividend? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)