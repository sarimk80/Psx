package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.model.SymbolsModel
import com.pizza.psx.domain.usecase.SymbolListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val symbolListUseCase: SymbolListUseCase
): ViewModel() {

    private val _uiState = mutableStateOf(SearchUiState())
    val uiState: State<SearchUiState> = _uiState

    fun getSymbolList(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {

                when(val result = symbolListUseCase()){
                    is StockResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            symbolList = result.data,
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
                    StockResult.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }

            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

}

data class SearchUiState(
    val symbolList: SymbolsModel? = null,
    val searchedSymbol:SymbolsModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)