package com.inFlow.moneyManager.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.shared.kotlin.FieldType
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class DashboardViewModel(private val repository: AppRepository) : ViewModel() {
    var activeFilters = MutableStateFlow(FiltersDto())
    val query = MutableStateFlow("")

    private val eventChannel = Channel<DashboardEvent>(Channel.BUFFERED)
    val eventFlow = eventChannel.receiveAsFlow()

    private val _balanceData = MutableStateFlow<Pair<Double, Double>?>(null)
    val balanceData: StateFlow<Pair<Double, Double>?> by this::_balanceData

    val transactionList = combine(activeFilters, query) { filters, query ->
        Pair(filters, query)
    }.flatMapLatest {
        repository.getTransactions(it.first, it.second)
    }

    fun fetchBalanceData() = viewModelScope.launch {
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
        _balanceData.value = Pair(incomes, expenses)
    }

    fun onAddClicked() = viewModelScope.launch {
        eventChannel.send(DashboardEvent.NavigateToAddTransaction)
    }
}

sealed class DashboardEvent {
    object NavigateToFilters: DashboardEvent()
    object NavigateToAddTransaction: DashboardEvent()
    object OpenFilters: DashboardEvent()
}

data class FieldError(val message: String, val field: FieldType)

enum class PeriodMode { WHOLE_MONTH, CUSTOM_RANGE }

enum class ShowTransactions { SHOW_EXPENSES, SHOW_INCOMES, SHOW_BOTH }

enum class SortBy(val sortName: String) {
    SORT_BY_DATE("Date"),
    SORT_BY_CATEGORY("Category"),
    SORT_BY_AMOUNT("Amount")
}
