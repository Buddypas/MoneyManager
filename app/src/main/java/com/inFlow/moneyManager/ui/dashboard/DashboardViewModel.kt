package com.inFlow.moneyManager.ui.dashboard

import android.view.MenuItem
import android.widget.CompoundButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.db.entities.Transaction
import com.inFlow.moneyManager.db.entities.TransactionsDao
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.shared.kotlin.*
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.Month
import java.time.Year

class DashboardViewModel(private val repository: AppRepository) : ViewModel() {
    var activeFilters = MutableStateFlow(FiltersDto())
    val searchQuery = MutableStateFlow("")
    var test = "Test"

    val transactionList = activeFilters.flatMapLatest {
        repository.getAllTransactions()
    }

    val transactions = transactionList.asLiveData()

    var year: Int = LocalDate.now().year
    var yearPosition = 0
    var monthPosition = LocalDate.now().monthValue - 1

    private val filtersEventChannel = Channel<FiltersEvent>()
    val filtersEvent = filtersEventChannel.receiveAsFlow()

    var period = PeriodMode.WHOLE_MONTH
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
    
//    init {
//        viewModelScope.launch {
//            repository.populateDb()
//        }
//    }

    fun setFilters(data: FiltersDto) {
        period = data.period
        fromDate = data.fromDate
        toDate = data.toDate
        sortBy = data.sortBy
        showIncomes = data.showIncomes
        showExpenses = data.showExpenses
        isDescending = data.isDescending
        if (period == PeriodMode.WHOLE_MONTH && fromDate != null) {
            monthPosition = fromDate!!.monthValue - 1
            year = fromDate!!.year
        }
    }

    fun onSortOrderChecked(checkedId: Int, isChecked: Boolean) {
        if (isChecked)
            isDescending = when (checkedId) {
                R.id.desc_btn -> true
                else -> false
            }
    }

    fun onPeriodSelected(checkedId: Int) = viewModelScope.launch {
        period =
            if (checkedId == R.id.custom_range_btn) PeriodMode.CUSTOM_RANGE
            else PeriodMode.WHOLE_MONTH
        filtersEventChannel.send(FiltersEvent.ChangePeriodMode(period))
    }

    fun onYearSelected(position: Int) {
        yearPosition = position
        year = years[position].toInt()
    }

    fun onMonthSelected(position: Int) {
        monthPosition = position
    }

    fun onTypeChecked(btn: CompoundButton, isChecked: Boolean) {
        when (btn.id) {
            R.id.incomes_cbx -> showIncomes = isChecked
            R.id.expenses_cbx -> showExpenses = isChecked
        }
    }

    fun onClearClicked() = viewModelScope.launch {
        setFilters(FiltersDto())
        filtersEventChannel.send(FiltersEvent.ClearFilters)
    }

    fun onApplyClicked() = viewModelScope.launch {
        val error = validateFilters()
        if (error == null) {
            val filtersData = FiltersDto(
                period,
                showIncomes,
                showExpenses,
                isDescending,
                sortBy,
                fromDate,
                toDate
            )
            filtersEventChannel.send(FiltersEvent.ApplyFilters(filtersData))
        } else filtersEventChannel.send(FiltersEvent.ShowFieldError(error))
    }

    /**
     * Returns null if there is no error. Won't validate dates that are after today and will instead return no data
     */
    private fun validateFilters(): FieldError? {
        if (period == PeriodMode.CUSTOM_RANGE) {
            fromDate = fromDateString.value.toLocalDate()
            toDate = toDateString.value.toLocalDate()
        } else {
            val month = Month.of(monthPosition + 1)
            val isLeapYear = Year.isLeap(year.toLong())
            fromDate = LocalDate.of(year, month, 1)
            toDate = LocalDate.of(year, month, month.length(isLeapYear))
            Timber.e("$fromDate - $toDate")
        }
        return when {
            fromDate == null -> {
                val fieldType =
                    if (period == PeriodMode.CUSTOM_RANGE)
                        FieldType.FIELD_DATE_FROM
                    else FieldType.FIELD_OTHER
                FieldError(
                    "Date is not valid",
                    fieldType
                )
            }
            toDate == null -> {
                val fieldType =
                    if (period == PeriodMode.CUSTOM_RANGE) FieldType.FIELD_DATE_TO else FieldType.FIELD_OTHER
                FieldError(
                    "Date is not valid",
                    fieldType
                )
            }
            fromDate?.isAfter(toDate) == true -> FieldError(
                "Dates are not valid",
                FieldType.FIELD_OTHER
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