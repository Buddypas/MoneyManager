package com.inFlow.moneyManager.ui.filters

import androidx.lifecycle.ViewModel
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.shared.kotlin.MONTHS
import com.inFlow.moneyManager.shared.kotlin.SORT_BY_AMOUNT
import com.inFlow.moneyManager.shared.kotlin.SORT_BY_CATEGORY
import com.inFlow.moneyManager.shared.kotlin.SORT_BY_DATE
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.time.LocalDate

class FiltersViewModel : ViewModel() {
    var year: Int = LocalDate.now().year
    var month: Int = LocalDate.now().monthValue - 1

    var period = MutableStateFlow(PeriodMode.WHOLE_MONTH)
    var isDescending = true
    var showIncomes: Boolean = true
    var showExpenses: Boolean = true
    var sortBy = SORT_BY_DATE
    var monthAndYear: LocalDate? = null
    var fromDate: LocalDate? = null
    var toDate: LocalDate? = null

    var fromDateString = MutableStateFlow("")
    var toDateString = MutableStateFlow("")

    val sortOptions = listOf(
        SORT_BY_DATE,
        SORT_BY_CATEGORY,
        SORT_BY_AMOUNT
    )

    val years = listOf(
        year.toString(),
        (year - 1).toString(),
        (year - 2).toString()
    )

    fun setFilters(data: FiltersDto) {
        period.value = data.period
        if(data.period == PeriodMode.WHOLE_MONTH) monthAndYear = data.monthAndYear
        else {
            fromDate = data.fromDate
            toDate = data.toDate
        }
        sortBy = data.sortBy
        showIncomes = data.showIncomes
        showExpenses = data.showExpenses
    }

    fun onSortOrderChecked(checkedId: Int, isChecked: Boolean) {
        if (isChecked)
            isDescending = when (checkedId) {
                R.id.desc_btn -> true
                else -> false
            }
    }

    fun onPeriodSelected(checkedId: Int) {
        period.value = when (checkedId) {
            R.id.custom_range_btn -> PeriodMode.CUSTOM_RANGE
            else -> PeriodMode.WHOLE_MONTH
        }
    }

    fun onYearSelected(position: Int) {
        year = years[position].toInt()
    }

    fun onMonthSelected(position: Int) {
        month = MONTHS[position].toInt()
    }
}

enum class PeriodMode { WHOLE_MONTH, CUSTOM_RANGE }