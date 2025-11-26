package com.example.psx.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psx.domain.model.Root
import com.example.psx.domain.model.Sector
import com.example.psx.domain.model.StockResult
import com.example.psx.domain.usecase.GainersUseCase
import com.example.psx.domain.usecase.SectorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SectorViewModel @Inject constructor(
    private val getSector: SectorUseCase
) : ViewModel(){
    private val _uiState = mutableStateOf(SectorUiState())
    val uiState: State<SectorUiState> = _uiState

    fun getSectorAll(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val answer = getSector()){
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


data class SectorUiState(
    val stocks: Sector? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)