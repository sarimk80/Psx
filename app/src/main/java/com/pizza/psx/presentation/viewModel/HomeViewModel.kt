package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.Root
import com.pizza.psx.domain.model.SectorResponse
import com.pizza.psx.domain.model.StockData
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.usecase.GainersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gainersUseCase: GainersUseCase,
) : ViewModel(){

    private val _uiState = mutableStateOf(HomeUiState())
    val uiState: State<HomeUiState> = _uiState

     fun getGainersAndLosers(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val answer = gainersUseCase()){
                is StockResult.Success -> {

                    val allStocks = answer.data.sectors.values.flatten()

                    // Get gainers (stocks with "increase" trend)
                    val gainers = allStocks
                        .filter { it.trend.lowercase() == "increase" }
                        .sortedByDescending {
                            it.change.replace(",", "").toDoubleOrNull() ?: 0.0
                        }
                        .take(12) // Top 10 gainers by change

                    // Get losers (stocks with "decrease" trend)
                    val losers = allStocks
                        .filter { it.trend.lowercase() == "decrease" }
                        .sortedBy {
                            it.change.replace(",", "").toDoubleOrNull() ?: 0.0
                        } // Sorted ascending (most negative first)
                        .take(12) // Top 10 losers by change

                    // Get most active stocks by volume
                    val active = allStocks
                        .sortedByDescending {
                            it.volume.replace(",", "").toLongOrNull() ?: 0L
                        }
                        .take(12) // Top 10 by volume


                    _uiState.value = _uiState.value.copy(
                        stocks = answer.data,
                        gainers = gainers,
                        losers = losers,
                        active = active,
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
                StockResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
}


data class HomeUiState(
    val stocks: SectorResponse? = null,
    val gainers:List<StockData>? = null,
    val losers:List<StockData>? = null,
    val active:List<StockData>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)