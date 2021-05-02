package com.inFlow.moneyManager.ui.dashboard

import android.widget.CompoundButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.shared.kotlin.*
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth

@ExperimentalCoroutinesApi
class DashboardViewModel(private val repository: AppRepository) : ViewModel() {
//    var activeFilters = MutableStateFlow(FiltersDto())

    var period = MutableStateFlow(PeriodMode.WHOLE_MONTH)
    var show: MutableStateFlow<ShowTransactions?> = MutableStateFlow(ShowTransactions.SHOW_BOTH)
    var yearMonth: MutableStateFlow<YearMonth?> = MutableStateFlow(YearMonth.now())
    var customRange: MutableStateFlow<Pair<LocalDate?, LocalDate?>> =
        MutableStateFlow(Pair(null, null))
    var sortBy = MutableStateFlow(SortBy.SORT_BY_DATE)
    var isDescending = MutableStateFlow(true)
    var searchQuery = MutableStateFlow("")


//    val transactionList = activeFilters.flatMapLatest {
//        repository.getAllTransactions()
//    }

    private val filtersEventChannel = Channel<FiltersEvent>()
    val filtersEvent = filtersEventChannel.receiveAsFlow()

    val sortOptions = listOf(
        "Date",
        "Category",
        "Amount"
    )

    val years = listOf(
        yearMonth.value!!.year.toString(),
        (yearMonth.value!!.year - 1).toString(),
        (yearMonth.value!!.year - 2).toString()
    )

    // TODO: Consider using state flows
    fun fetchBalanceData() = flow {
        val expenseList = repository.getAllExpenses()
        var expenses = 0.0
        if (expenseList.isNotEmpty()) expenseList.forEach {
            expenses -= it.transactionAmount
        }

        val incomeList = repository.getAllIncomes()
        var incomes = 0.0
        if (incomeList.isNotEmpty()) incomeList.forEach {
            incomes += it.transactionAmount
        }
        emit(Pair(incomes, expenses))
    }

//    init {
//        viewModelScope.launch {
//            repository.populateDb()
//        }
//    }

    fun onSortOrderChecked(checkedId: Int, isChecked: Boolean) {
        if (isChecked)
            isDescending.value = when (checkedId) {
                R.id.desc_btn -> true
                else -> false
            }
    }

    fun onPeriodSelected(checkedId: Int) = viewModelScope.launch {
        period.value =
            if (checkedId == R.id.custom_range_btn) PeriodMode.CUSTOM_RANGE
            else PeriodMode.WHOLE_MONTH
                .also { filtersEventChannel.send(FiltersEvent.ChangePeriodMode(it)) }
    }

    fun onYearSelected(year: Int) {
        val prevMonth = yearMonth.value?.month
        val newYearMonth = YearMonth.of(year, prevMonth)
        yearMonth.value = newYearMonth
    }

    fun onMonthSelected(monthPosition: Int) {
        val prevYear = yearMonth.value?.year
        prevYear?.let {
            val newYearMonth = YearMonth.of(it, monthPosition + 1)
            yearMonth.value = newYearMonth
        }
    }

    fun onTypeChecked(btn: CompoundButton, isChecked: Boolean) {
        when (btn.id) {
            R.id.incomes_cbx -> when (show.value) {
                ShowTransactions.SHOW_EXPENSES -> if (isChecked) show.value =
                    ShowTransactions.SHOW_BOTH
                ShowTransactions.SHOW_INCOMES -> if (!isChecked) show.value = null
                ShowTransactions.SHOW_BOTH -> if (!isChecked) show.value =
                    ShowTransactions.SHOW_EXPENSES
            }
            R.id.expenses_cbx -> when (show.value) {
                ShowTransactions.SHOW_EXPENSES -> if (!isChecked) show.value =
                    null
                ShowTransactions.SHOW_INCOMES -> if (isChecked) show.value =
                    ShowTransactions.SHOW_BOTH
                ShowTransactions.SHOW_BOTH -> if (!isChecked) show.value =
                    ShowTransactions.SHOW_INCOMES
            }
        }
    }

    fun onClearClicked() = viewModelScope.launch {
        clearFilters()
        filtersEventChannel.send(FiltersEvent.ClearFilters)
    }

    fun onApplyClicked() = viewModelScope.launch {
        val error = validateFilters()
        if (error == null)
            FiltersDto(
                period = period.value,
                show = show.value!!,
                yearMonth =
                if (period.value == PeriodMode.WHOLE_MONTH) yearMonth.value
                else null,
                customRange =
                if (period.value == PeriodMode.CUSTOM_RANGE) customRange.value
                else Pair(null, null),
                sortBy = sortBy.value,
                isDescending = isDescending.value,
                searchQuery = searchQuery.value
            ).also {
                filtersEventChannel.send(FiltersEvent.ApplyFilters(it))
            }
        else filtersEventChannel.send(FiltersEvent.ShowFieldError(error))
    }

    private fun clearFilters() {
        period.value = PeriodMode.WHOLE_MONTH
        show.value = ShowTransactions.SHOW_BOTH
        yearMonth.value = YearMonth.now()
        customRange.value = Pair(null, null)
        sortBy.value = SortBy.SORT_BY_DATE
        isDescending.value = true
        searchQuery.value = ""
    }

    /**
     * Returns null if there is no error. Won't validate dates that are after today and will instead return no data
     */
    private fun validateFilters(): FieldError? {
        if (show.value == null) return FieldError(
            "Incomes or expenses must be selected",
            FieldType.FIELD_OTHER
        )
        if (period.value == PeriodMode.CUSTOM_RANGE) {
            if (customRange.value.first == null) return FieldError(
                "Date is not valid",
                FieldType.FIELD_DATE_FROM
            )
            if (customRange.value.second == null) return FieldError(
                "Date is not valid",
                FieldType.FIELD_DATE_TO
            )
            if (customRange.value.first!!.isAfter(customRange.value.second)) return FieldError(
                "Dates are not valid",
                FieldType.FIELD_OTHER
            )
        } else if (yearMonth.value == null) return FieldError(
            "Month is not valid",
            FieldType.FIELD_OTHER
        )
        return null
    }
}

data class FieldError(val message: String, val field: FieldType)

sealed class FiltersEvent {
    object ClearFilters : FiltersEvent()
//    data class ApplyFilters(val filtersData: FiltersDto) : FiltersEvent()
    data class ChangePeriodMode(val newPeriodMode: PeriodMode) : FiltersEvent()
    data class ShowFieldError(val fieldError: FieldError) : FiltersEvent()
}

enum class PeriodMode { WHOLE_MONTH, CUSTOM_RANGE }
enum class ShowTransactions { SHOW_EXPENSES, SHOW_INCOMES, SHOW_BOTH }

enum class SortBy { SORT_BY_DATE, SORT_BY_CATEGORY, SORT_BY_AMOUNT }