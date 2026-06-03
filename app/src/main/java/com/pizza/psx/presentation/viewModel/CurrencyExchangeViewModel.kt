package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.CurrencyExchangeModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.usecase.CurrencyExchangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CurrencyExchangeViewModel@Inject constructor(
    private val currencyExchangeUseCase: CurrencyExchangeUseCase
): ViewModel() {

    private val _uiState = mutableStateOf(CurrencyExchangeUiState())
    val uiState: State<CurrencyExchangeUiState> = _uiState


    fun getAllCurrency(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when(val result = currencyExchangeUseCase()){
                is StockResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currency = result.data
                    )
                }
                is StockResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }

                is StockResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }

            }
        }
    }

}

data class CurrencyExchangeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currency: CurrencyExchangeModel? = null,
)