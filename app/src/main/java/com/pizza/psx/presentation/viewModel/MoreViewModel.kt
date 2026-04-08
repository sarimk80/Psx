package com.pizza.psx.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizza.psx.domain.repo.PortfolioRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val repo:PortfolioRepo,
):ViewModel() {

    fun clearAllData(){
        viewModelScope.launch {
            repo.clearAllData()
        }
    }
}