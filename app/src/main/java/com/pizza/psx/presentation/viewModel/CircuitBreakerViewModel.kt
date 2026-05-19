package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.CircuitBreakerModel
import com.pizza.psx.domain.model.Companies
import com.pizza.psx.domain.model.Etf
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.usecase.AllCircuitBreakerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CircuitBreakerViewModel@Inject constructor(
    private val breakerViewModel: AllCircuitBreakerUseCase
): ViewModel() {

    private val _uiState = mutableStateOf(BreakerUiState())
    val uiState: State<BreakerUiState> = _uiState

    fun getAllCircuitBreaker(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when(val result = breakerViewModel()){
                is StockResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        breaker = result.data
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

data class BreakerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val breaker: CircuitBreakerModel? = null,
)