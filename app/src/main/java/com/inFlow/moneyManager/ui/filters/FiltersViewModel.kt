package com.inFlow.moneyManager.ui.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.shared.kotlin.*
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.Year

class FiltersViewModel : ViewModel() {
    var year: Int = LocalDate.now().year
    var month: Int = LocalDate.now().monthValue - 1
    var yearPosition = 0
    var monthPosition = 0

    private val filtersEventChannel = Channel<FiltersEvent>()
    val filtersEvent = filtersEventChannel.receiveAsFlow()

    var period = MutableStateFlow(PeriodMode.WHOLE_MONTH)
    var isDescending = true
    var showIncomes: Boolean = true
    var showExpenses: Boolean = true
    var sortBy = SORT_BY_DATE
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
        fromDate = data.fromDate
        toDate = data.toDate
        sortBy = data.sortBy
        showIncomes = data.showIncomes
        showExpenses = data.showExpenses
    }

    private fun clear() = setFilters(FiltersDto())

    fun onSortOrderChecked(checkedId: Int, isChecked: Boolean) {
        if (isChecked)
            isDescending = when (checkedId) {
                R.id.desc_btn -> true
                else -> false
            }
    }

    fun onPeriodSelected(checkedId: Int) = viewModelScope.launch {
        when (checkedId) {
            R.id.custom_range_btn -> period.value = PeriodMode.CUSTOM_RANGE
            else -> period.value = PeriodMode.WHOLE_MONTH
        }
        filtersEventChannel.send(FiltersEvent.ChangePeriodMode(period.value))
    }

    fun onYearSelected(position: Int) {
        yearPosition = position
        year = years[position].toInt()
    }

    fun onMonthSelected(position: Int) {
        monthPosition = position
        month = MONTHS[position].toInt()
    }

    fun onClearClicked() = viewModelScope.launch {
        clear()
        filtersEventChannel.send(FiltersEvent.ClearFilters)
    }

    fun onApplyClicked() = viewModelScope.launch {
        val error = validateFilters()
        if(error == null) {
            val filtersData = FiltersDto(
                period.value,
                showIncomes,
                showExpenses,
                isDescending,
                sortBy,
                fromDate,
                toDate
            )
            filtersEventChannel.send(FiltersEvent.ApplyFilters(filtersData))
        }
        else filtersEventChannel.send(FiltersEvent.ShowFieldError(error))
    }

    /**
     * Returns null if there is no error
     */
    private fun validateFilters(): FieldError? {
        if (period.value == PeriodMode.CUSTOM_RANGE) {
            fromDate = fromDateString.value.toLocalDate()
            toDate = toDateString.value.toLocalDate()
        } else {
            val month = Month.of(monthPosition + 1)
            val isLeapYear = Year.isLeap(year.toLong())
            fromDate = LocalDate.of(year, month, 1)
            toDate = LocalDate.of(year, month, month.length(isLeapYear))
        }
        val today = LocalDate.now()
        return when {
            fromDate == null || fromDate!!.isAfter(today) ->
                FieldError(
                    "Date is not valid",
                    FieldType.FIELD_DATE_FROM
                )
            toDate == null || toDate!!.isAfter(today) -> FieldError(
                "Date is not valid",
                FieldType.FIELD_DATE_TO
            )
            // TODO: Find better message
            !showIncomes && !showExpenses -> FieldError(
                "Incomes or expenses must be selected",
                FieldType.FIELD_OTHER
            )
            else -> null
        }
    }
}

data class FieldError(val message: String, val field: FieldType)

sealed class FiltersEvent {
    object ClearFilters : FiltersEvent()
    data class ApplyFilters(val filtersData: FiltersDto) : FiltersEvent()
    data class ChangePeriodMode(val newPeriodMode: PeriodMode) : FiltersEvent()
    data class ShowFieldError(val fieldError: FieldError) : FiltersEvent()
}

enum class PeriodMode { WHOLE_MONTH, CUSTOM_RANGE }