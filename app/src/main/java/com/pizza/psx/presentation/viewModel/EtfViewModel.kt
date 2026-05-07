package com.pizza.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.model.Etf
import com.pizza.psx.domain.model.EtfModel
import com.pizza.psx.domain.model.PortfolioModel
import com.pizza.psx.domain.model.StockResult
import com.pizza.psx.domain.usecase.EtfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EtfViewModel @Inject constructor(
    private val etfUseCase: EtfUseCase
)
    : ViewModel() {
    private val _uiState = mutableStateOf(EtfUiState())
    val uiState: State<EtfUiState> = _uiState

    fun getAllEtf(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when(val result = etfUseCase()){
                is StockResult.Success -> {
                    val groupEtf =  result.data.etfs.groupBy { it.type }
                    _uiState.value = _uiState.value.copy(
                        etfModel = result.data,
                        groupEtf = groupEtf,
                        isLoading = false,
                        error = null
                    )
                }
                is StockResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message )
                }
                StockResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

}


data class EtfUiState(
    val etfModel: EtfModel? = null,
    val groupEtf: Map<String, List<Etf>>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)