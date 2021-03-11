package com.inFlow.moneyManager.ui.filters

import androidx.lifecycle.ViewModel
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class FiltersViewModel : ViewModel() {
    lateinit var activeFilters: FiltersDto
    lateinit var initialFilters: FiltersDto

//    val periodMode = MutableStateFlow<PeriodMode>(initialFilters?.period ?: PeriodMode.WHOLE_MONTH)
//    val showIncomes = MutableStateFlow<Boolean>(initialFilters?.showIncomes ?: true)
//    val showExpenses = MutableStateFlow<Boolean>(initialFilters?.showExpenses ?: true)

    fun onSortOrderChecked(checkedId: Int, isChecked: Boolean) {
        if (isChecked)
            activeFilters.isDescending = when (checkedId) {
                R.id.desc_btn -> true
                else -> false
            }
    }

    fun onPeriodSelected(checkedId: Int) {
        activeFilters.period = when (checkedId) {
            R.id.custom_range_btn ->  PeriodMode.CUSTOM_RANGE
            else -> PeriodMode.WHOLE_MONTH
        }
    }
}

enum class PeriodMode { WHOLE_MONTH, CUSTOM_RANGE }