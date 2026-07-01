package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.Companies
import com.pizza.psx.domain.model.CompaniesData
import com.pizza.psx.domain.model.Dividend
import com.pizza.psx.domain.model.DividendModel
import com.pizza.psx.domain.model.FinancialStats
import com.pizza.psx.domain.model.FreeFloat
import com.pizza.psx.domain.model.FundamentalData
import com.pizza.psx.domain.model.Fundamentals
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.MarketDividend
import com.pizza.psx.domain.model.PsxOhlcModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.SymbolDetail
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.usecase.AllIndicesUseCase
import com.pizza.psx.domain.usecase.CompanyDividendUseCase
import com.pizza.psx.domain.usecase.CompanyFundamentalUseCase
import com.pizza.psx.domain.usecase.CompanyUseCase
import com.pizza.psx.domain.usecase.KLineModelUseCase
import com.pizza.psx.domain.usecase.MarketDividendUseCase
import com.pizza.psx.domain.usecase.SymbolDetailUseCase
import com.pizza.psx.domain.usecase.TickerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TickerDetailViewModel@Inject constructor(
    private val getTickerDetail: TickerUseCase,
    //private val getCompanyDetail:CompanyUseCase,
    private val getCompanyFundamental:CompanyFundamentalUseCase,
    private val getCompanyDividend: CompanyDividendUseCase,
    private val marketDividendUseCase: MarketDividendUseCase,
    private val getKLineModelUseCase: KLineModelUseCase,
    private val getSymbolDetailUseCase: SymbolDetailUseCase,
    private val getAllIndicesUseCase: AllIndicesUseCase

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
                //val companyDetail = async { getCompanyDetail(symbol) }
                val companyFundamental = async { getCompanyFundamental(symbol) }
                val companyDividend = async { getCompanyDividend(symbol) }
                val kLine = async { getKLineModelUseCase(symbol) }
                val symbolDetailOverview = async { getSymbolDetailUseCase(symbol) }

//                val (tickerResult,companyResult,fundamentalResult,dividendResult,kLineResult,symbolDetailResult) =
//                    awaitAll(tickerDetail,companyDetail,companyFundamental,companyDividend,kLine,symbolDetailOverview)

                val results = awaitAll(tickerDetail, companyFundamental, companyDividend, kLine, symbolDetailOverview)

                val tickerResult = results[0] as StockResult<Ticker>
                //val companyResult = results[1] as StockResult<Companies>
                val fundamentalResult = results[1] as StockResult<Fundamentals>
                val dividendResult = results[2] as StockResult<Dividend>
                val kLineResult = results[3] as StockResult<PsxOhlcModel>
                val symbolDetailResult = results[4] as StockResult<SymbolDetail>

                val ticker = when(tickerResult){
                    is StockResult.Success -> tickerResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = tickerResult.message)
                    }
                }

//                val company = when(companyResult){
//                    is StockResult.Success -> companyResult.data
//                    is StockResult.Loading -> null
//                    is StockResult.Error ->{
//                        _uiState.value = _uiState.value.copy(error = companyResult.message)
//                    }
//                }

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

                val resultSymbolOverview = when(symbolDetailResult){
                    is StockResult.Success -> symbolDetailResult.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = symbolDetailResult.message)
                    }
                }


                val tickerResponse = ticker as Ticker

                _uiState.value = _uiState.value.copy(
                    stocks = ticker as Ticker,
                    company = Companies(
                        success = true, data = CompaniesData(
                            symbol = tickerResponse.data.symbol,
                            scrapedAt = "", financialStats =
                                FinancialStats(
                                    marketCap = FreeFloat(
                                        raw = tickerResponse.data.market_cap.toString(),
                                        numeric = tickerResponse.data.market_cap
                                    ),
                                    shares = FreeFloat(
                                        raw = tickerResponse.data.shares.toString(),
                                        numeric = tickerResponse.data.shares
                                    ),
                                    freeFloat = FreeFloat(
                                        raw = tickerResponse.data.free_float.toString(),
                                        numeric = tickerResponse.data.free_float
                                    ),
                                    freeFloatPercent = FreeFloat(
                                        raw = tickerResponse.data.free_float_share.toString(),
                                        numeric = tickerResponse.data.free_float_share
                                    ),
                                ),

                            businessDescription = tickerResponse.data.business_description,
                            keyPeople = tickerResponse.data.key_people,
                            error = ""
                        ),
                        timestamp = System.currentTimeMillis()
                    ) ,
                    fundamentals= Fundamentals(
                        success = true,
                        data = FundamentalData(
                            symbol = tickerResponse.data.symbol,
                            sector = "",
                            listedIn = "",
                            marketCap = tickerResponse.data.market_cap.toString(),
                            price = tickerResponse.data.price,
                            changePercent = tickerResponse.data.changePercent,
                            yearChange = tickerResponse.data.year_1_change,
                            peRatio = 0.0,
                            dividendYield = 0.0,
                            freeFloat = tickerResponse.data.free_float.toString(),
                            volume30Avg = 0.0,
                            isNonCompliant = true,
                            timestamp = System.currentTimeMillis().toString()
                        ),
                        timestamp = System.currentTimeMillis()
                    ),
                    dividend = tickerResponse.data.dividend,
                    kLine = resultKLine as PsxOhlcModel,
                    symbolDetail = resultSymbolOverview as SymbolDetail,
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
               when (val result = getAllIndicesUseCase()) {

                   is StockResult.Success -> {

                       val requiredSymbols = setOf(
                           "KSE100",
                           "KSE30",
                           "PSXDIV20",
                           "MII30",
                           "KMI30"
                       )

                       val filteredList = result.data.filter {
                           it.data.symbol in requiredSymbols
                       }

                       _uiState.value = _uiState.value.copy(
                           listOfTicker = filteredList,
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

                   is StockResult.Loading -> {
                       _uiState.value = _uiState.value.copy(isLoading = true)
                   }
               }

//               val ticketResult = symbol.map { sym ->
//
//                   async { getTickerDetail(type = "IDX", symbol = sym) }
//               }.awaitAll()
//
//
//            val tickerResponse = ticketResult.mapNotNull { result ->
//                if (result is StockResult.Success) result.data else null
//            }
//
//               _uiState.value = _uiState.value.copy(
//                   listOfTicker = tickerResponse,
//                   isLoading = false,
//                   error = null
//               )

           }catch (e:Exception){
               _uiState.value = _uiState.value.copy(
                   isLoading = false,
                   error = e.message
               )
           }

       }
   }

//    fun getCompanyDetailAll(type:String,symbol:String){
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true)
//            when (val answer = getCompanyDetail(symbol)){
//                is StockResult.Success -> {
//                    _uiState.value = _uiState.value.copy(
//                        company = answer.data,
//                        isLoading = false,
//                        error = null
//                    )
//                }
//                is StockResult.Error -> {
//                    _uiState.value = _uiState.value.copy(
//                        isLoading = false,
//                        error = answer.message
//                    )
//                }
//                is  StockResult.Loading -> {
//                    _uiState.value = _uiState.value.copy(isLoading = true)
//                }
//            }
//        }
//    }

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
                val result = getKLineModelUseCase(symbol)

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
    val dividend: List<DividendModel>? = null,
    val marketDividend:List<MarketDividend>? = null,
    val symbolDetail: SymbolDetail? = null,
    val kLine: PsxOhlcModel? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isDividendLoading:Boolean = true,
)


data class KLineUiState(
    val isLoading:Boolean = true,
    val error: String? = null,
    val kLine: PsxOhlcModel? = null,

    )
