package com.inFlow.moneyManager.ui.filters

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class FiltersViewModel: ViewModel() {
    val currentPeriodMode = MutableStateFlow(PeriodMode.WHOLE_MONTH)
    val isDescending = MutableStateFlow(true)
}

enum class PeriodMode { WHOLE_MONTH, CUSTOM_RANGE }