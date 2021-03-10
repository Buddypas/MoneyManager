package com.inFlow.moneyManager.ui.dashboard

import androidx.lifecycle.ViewModel
import com.inFlow.moneyManager.ui.filters.PeriodMode
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.flow.MutableStateFlow

class DashboardViewModel() : ViewModel() {
    var activeFilters = MutableStateFlow(FiltersDto())
    val searchQuery = MutableStateFlow<String>("")
}