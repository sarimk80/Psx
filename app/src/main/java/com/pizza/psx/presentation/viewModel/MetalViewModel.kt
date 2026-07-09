package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.CurrencyExchangeModel
import com.pizza.psx.domain.model.MetalsModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.usecase.CurrencyExchangeUseCase
import com.pizza.psx.domain.usecase.MetalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetalViewModel@Inject constructor(
    private val breakerViewModel: MetalsUseCase,
    private val currencyExchangeUseCase: CurrencyExchangeUseCase
): ViewModel() {

    private val _uiState = mutableStateOf(MetalUiState())
    val uiState: State<MetalUiState> = _uiState

    fun getMetal(metal: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val metalResult = async {  breakerViewModel(metal) }
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

                val updatedMetals = metalData.map {
                    it.copy(
                        max_price = (it.max_price.toDouble() * usdCurrency!!.currency).toString()
                    )
                }

                _uiState.value = _uiState.value.copy(isLoading = false, metals = updatedMetals )

            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.toString())
            }







//            when(val result = breakerViewModel(metal)){
//                is StockResult.Success -> {
//                    _uiState.value = _uiState.value.copy(
//                        isLoading = false,
//                        breaker = result.data
//                    )
//                }
//                is StockResult.Loading -> {
//                    _uiState.value = _uiState.value.copy(isLoading = true)
//                }
//
//                is StockResult.Error -> {
//                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
//                }
//
//            }
        }
    }

}


data class MetalUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val metals: List<MetalsModel>? = null,
)