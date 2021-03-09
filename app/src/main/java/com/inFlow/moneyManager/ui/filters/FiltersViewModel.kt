package com.inFlow.moneyManager.ui.filters

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inFlow.moneyManager.ui.filters.FiltersDialog.Companion.WHOLE_MONTH

class FiltersViewModel: ViewModel() {
    val currentPeriodMode = MutableLiveData(WHOLE_MONTH)
}