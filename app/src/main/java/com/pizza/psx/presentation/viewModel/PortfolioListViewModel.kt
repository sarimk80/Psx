package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.PortfolioWithTransactions
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.repo.PortfolioRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioListViewModel@Inject constructor(
    private val repo:PortfolioRepo,
    savedStateHandle: SavedStateHandle
):ViewModel() {

    private val _uiState = mutableStateOf(PortfolioListUiState())

    val uiState: State<PortfolioListUiState> = _uiState
    private val symbol: String = checkNotNull(savedStateHandle["symbol"])

    init {
        getTickerAllTransaction()
    }

    private fun getTickerAllTransaction(){
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            repo.getSymbolAllTransaction(symbol)
                .collect { list ->
                    _uiState.value = PortfolioListUiState(
                        isLoading = false,
                        listOfStocks = list,
                        error = null
                    )
                }
        }
    }
}


data class PortfolioListUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val listOfStocks:List<PortfolioWithTransactions>?=null
)