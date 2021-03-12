package com.inFlow.moneyManager.ui.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.shared.kotlin.MONTHS
import com.inFlow.moneyManager.shared.kotlin.SORT_BY_AMOUNT
import com.inFlow.moneyManager.shared.kotlin.SORT_BY_CATEGORY
import com.inFlow.moneyManager.shared.kotlin.SORT_BY_DATE
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

class FiltersViewModel : ViewModel() {
    var year: Int = LocalDate.now().year
    var month: Int = LocalDate.now().monthValue - 1

    private val filtersEventChannel = Channel<FiltersEvent>()
    val filtersEvent = filtersEventChannel.receiveAsFlow()

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
//        period.value = PeriodMode.CUSTOM_RANGE
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
        when (checkedId) {
            R.id.custom_range_btn -> period.value = PeriodMode.CUSTOM_RANGE
            else -> period.value = PeriodMode.WHOLE_MONTH
        }
        viewModelScope.launch { filtersEventChannel.send(FiltersEvent.PeriodEvent(period.value)) }
    }

    fun onYearSelected(position: Int) {
        year = years[position].toInt()
    }

    fun onMonthSelected(position: Int) {
        month = MONTHS[position].toInt()
    }
}

sealed class FiltersEvent {
    data class PeriodEvent(val newPeriodMode: PeriodMode) : FiltersEvent()
}

enum class PeriodMode { WHOLE_MONTH, CUSTOM_RANGE }