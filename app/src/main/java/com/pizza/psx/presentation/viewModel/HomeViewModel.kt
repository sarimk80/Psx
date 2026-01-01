package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.Root
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
                    _uiState.value = _uiState.value.copy(
                        stocks = answer.data,
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
    val stocks: Root? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)