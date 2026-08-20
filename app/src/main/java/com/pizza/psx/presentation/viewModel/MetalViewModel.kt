package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.CurrencyExchangeModel
import com.pizza.psx.domain.model.MetalList
import com.pizza.psx.domain.model.MetalsModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.usecase.CurrencyExchangeUseCase
import com.pizza.psx.domain.usecase.GetAllMetalListDetailUseCase
import com.pizza.psx.domain.usecase.GetAllMetalListUseCase
import com.pizza.psx.domain.usecase.MetalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetalViewModel@Inject constructor(
    private val breakerViewModel: MetalsUseCase,
    private val currencyExchangeUseCase: CurrencyExchangeUseCase,
    private val getAllMetalList: GetAllMetalListUseCase,
    private val getMetallistDetail: GetAllMetalListDetailUseCase,
): ViewModel() {

    private val _uiState = mutableStateOf(MetalUiState())
    val uiState: State<MetalUiState> = _uiState

    private val _uiMetalState = mutableStateOf(MetalListUiState())
    val uiMetalState: State<MetalListUiState> = _uiMetalState

    private val _uiUnitState = mutableStateOf(UnitUiState())
    val uiUnitState: State<UnitUiState> = _uiUnitState

    fun getMetalList(){

        viewModelScope.launch {

            _uiMetalState.value = _uiMetalState.value.copy(isLoading = true)

            try {
                val metallist = async { getAllMetalList() }

                val metalListResponse = metallist.await()

                val metalListResult = when(metalListResponse) {
                    is StockResult.Error -> null
                    StockResult.Loading -> {
                        _uiMetalState.value = _uiMetalState.value.copy(isLoading = true)
                    }

                    is StockResult.Success<*> -> metalListResponse.data
                }

                _uiMetalState.value = _uiMetalState.value.copy(metalList = metalListResult as MetalList)
            }catch (e: Exception){
                _uiMetalState.value = _uiMetalState.value.copy(error = e.toString())

            }
        }


    }

    fun getMetal(metal: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val metalResult = async {  getMetallistDetail(metal) }
                val currencyResult =async {  currencyExchangeUseCase() }

                val metalResponse = metalResult.await()
                val currencyResponse = currencyResult.await()


                val metals = when(metalResponse){
                    is StockResult.Success -> metalResponse.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = metalResponse.message)
                    }
                }

                val currency = when(currencyResponse){
                    is StockResult.Success -> currencyResponse.data
                    is StockResult.Loading -> null
                    is StockResult.Error ->{
                        _uiState.value = _uiState.value.copy(error = currencyResponse.message)
                    }
                }

                val currencyData  = currency as CurrencyExchangeModel
                val metalData = metals as List<MetalsModel>

                val usdCurrency = currencyData.response.firstOrNull { it.currencyName == "USD" }

                val PKR_PER_USD = usdCurrency?.currency ?: 1.0
                val OUNCE_TO_TOLA = 2.6666667

                val updatedMetals = metalData.map {
                    val pricePerOzPKR = it.high?.toDouble()?.times(PKR_PER_USD)
                    it.copy(
                        high = String.format("%.2f", pricePerOzPKR),
                    )
                }

                _uiState.value = _uiState.value.copy(isLoading = false, metals = updatedMetals )

            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.toString())
            }
        }
    }

    fun changeGoldPrice(
        price: Double,
        unit: Units,
        karat: Karat
    ) {
        val unitPrice = when (unit) {
            Units.Ounce -> price
            Units.Gram -> price / 31.1034768
            Units.Tola -> price / 2.6666667
            Units.Kilo -> price * 32.1507466
            Units.Ratti -> price / 256.0
            Units.Masha -> price / 32.0
        }

        val karatPrice = when (karat) {
            Karat.K_24 -> unitPrice
            Karat.K_22 -> unitPrice * 22 / 24
            Karat.k_21 -> unitPrice * 21 / 24
            Karat.K_18 -> unitPrice * 18 / 24
        }

        _uiUnitState.value = _uiUnitState.value.copy(
            price = karatPrice,
            unit = unit,
            karat = karat
        )
    }

    fun resetValue(price: Double){
        _uiUnitState.value = _uiUnitState.value.copy(price = price)
    }

}


enum class Units {
    Ounce,
    Gram,
    Tola,
    Kilo,
    Ratti,
    Masha
}


enum class Karat {
    K_24,
    K_22,
    k_21,
    K_18
}

data class MetalUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val metals: List<MetalsModel>? = null,
)

data class MetalListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val metalList: MetalList? = null,
)

data class UnitUiState(
    val price: Double? = null,
    val unit: Units? = null,
    val karat: Karat? = null,
)