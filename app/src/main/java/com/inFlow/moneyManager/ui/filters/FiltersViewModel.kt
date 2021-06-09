package com.inFlow.moneyManager.ui.filters

import android.widget.CompoundButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.shared.kotlin.FieldType
import com.inFlow.moneyManager.shared.kotlin.toLocalDate
import com.inFlow.moneyManager.ui.dashboard.*
import com.inFlow.moneyManager.vo.FiltersDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@HiltViewModel
class FiltersViewModel : ViewModel() {
    lateinit var filters: FiltersDto

    var fromDateString = MutableStateFlow("")
    var toDateString = MutableStateFlow("")

    private val filtersEventChannel = Channel<FiltersEvent>()
    val filtersEvent = filtersEventChannel.receiveAsFlow()

    val sortOptions = listOf(
        SortBy.SORT_BY_DATE.sortName,
        SortBy.SORT_BY_CATEGORY.sortName,
        SortBy.SORT_BY_AMOUNT.sortName
    )

    val years: List<String> = listOf(
        LocalDate.now().year.toString(),
        (LocalDate.now().year - 1).toString(),
        (LocalDate.now().year - 2).toString()
    )

    fun setInitialFilters(data: FiltersDto) {
        val f = FiltersDto(
            data.period,
            data.show,
            if (data.period == PeriodMode.WHOLE_MONTH) data.yearMonth!! else null,
            if (data.period == PeriodMode.CUSTOM_RANGE) data.customRange else Pair(null, null),
            data.sortBy,
            data.isDescending
        )
        filters = f
    }

    fun onYearSelected(year: Int) {
        val prevMonth = filters.yearMonth?.month
        val newYearMonth = YearMonth.of(year, prevMonth)
        filters.yearMonth = newYearMonth
    }

    fun onMonthSelected(monthPosition: Int) {
        val prevYear = filters.yearMonth?.year
        prevYear?.let {
            val newYearMonth = YearMonth.of(it, monthPosition + 1)
            filters.yearMonth = newYearMonth
        }
    }

    fun onTypeChecked(btn: CompoundButton, isChecked: Boolean) {
        when (btn.id) {
            R.id.incomes_cbx -> when (filters.show) {
                ShowTransactions.SHOW_EXPENSES -> if (isChecked) filters.show =
                    ShowTransactions.SHOW_BOTH
                ShowTransactions.SHOW_INCOMES -> if (!isChecked) filters.show = null
                ShowTransactions.SHOW_BOTH -> if (!isChecked) filters.show =
                    ShowTransactions.SHOW_EXPENSES
                null -> if (isChecked) filters.show = ShowTransactions.SHOW_INCOMES
            }
            R.id.expenses_cbx -> when (filters.show) {
                ShowTransactions.SHOW_EXPENSES -> if (!isChecked) filters.show =
                    null
                ShowTransactions.SHOW_INCOMES -> if (isChecked) filters.show =
                    ShowTransactions.SHOW_BOTH
                ShowTransactions.SHOW_BOTH -> if (!isChecked) filters.show =
                    ShowTransactions.SHOW_INCOMES
                null -> if (isChecked) filters.show = ShowTransactions.SHOW_EXPENSES
            }
        }
    }

    fun onSortOrderChecked(checkedId: Int, isChecked: Boolean) {
        if (isChecked)
            filters.isDescending = when (checkedId) {
                R.id.desc_btn -> true
                else -> false
            }
    }

    /**
     * Returns null if there is no error. Won't validate dates that are after today and will instead return no data
     */
    private fun validateFilters(): FieldError? {
        if (filters.show == null) return FieldError(
            "Incomes or expenses must be selected",
            FieldType.FIELD_OTHER
        )
        if (filters.period == PeriodMode.CUSTOM_RANGE) {
            val from = fromDateString.value.toLocalDate()
            val to = toDateString.value.toLocalDate()
            filters.customRange = Pair(from, to)
            if (filters.customRange.first == null) return FieldError(
                "Date is not valid",
                FieldType.FIELD_DATE_FROM
            )
            if (filters.customRange.second == null) return FieldError(
                "Date is not valid",
                FieldType.FIELD_DATE_TO
            )
            if (filters.customRange.first!!.isAfter(filters.customRange.second)) return FieldError(
                "Dates are not valid",
                FieldType.FIELD_OTHER
            )
        } else if (filters.yearMonth == null) return FieldError(
            "Month is not valid",
            FieldType.FIELD_OTHER
        )
        return null
    }

    fun onApplyClicked() = viewModelScope.launch {
        val error = validateFilters()
        if (error == null)
            FiltersDto(
                period = filters.period,
                show = filters.show,
                yearMonth =
                if (filters.period == PeriodMode.WHOLE_MONTH) filters.yearMonth
                else null,
                customRange =
                if (filters.period == PeriodMode.CUSTOM_RANGE) filters.customRange
                else Pair(null, null),
                sortBy = filters.sortBy,
                isDescending = filters.isDescending
            ).also {
                filtersEventChannel.send(FiltersEvent.ApplyFilters(it))
            }
        else filtersEventChannel.send(FiltersEvent.ShowFieldError(error))
    }

    fun onClearClicked() = viewModelScope.launch {
        filters = FiltersDto()
        filtersEventChannel.send(FiltersEvent.ClearFilters)
    }
}

sealed class FiltersEvent {
    object ClearFilters : FiltersEvent()
    data class ApplyFilters(val filtersData: FiltersDto) : FiltersEvent()
    data class ShowFieldError(val fieldError: FieldError) : FiltersEvent()
}