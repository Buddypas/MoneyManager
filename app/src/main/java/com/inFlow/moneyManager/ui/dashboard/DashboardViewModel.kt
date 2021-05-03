package com.inFlow.moneyManager.ui.dashboard

import androidx.lifecycle.ViewModel
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.shared.kotlin.FieldType
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
class DashboardViewModel(private val repository: AppRepository) : ViewModel() {
    var activeFilters = MutableStateFlow(FiltersDto())
    val query = MutableStateFlow("")

    val transactionList = combine(activeFilters, query) { filters, query ->
        Pair(filters, query)
    }.flatMapLatest { repository.getTransactions(it.first, it.second) }

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


}

data class FieldError(val message: String, val field: FieldType)

enum class PeriodMode { WHOLE_MONTH, CUSTOM_RANGE }

enum class ShowTransactions { SHOW_EXPENSES, SHOW_INCOMES, SHOW_BOTH }
enum class SortBy(val sortName: String) {
    SORT_BY_DATE("Date"),
    SORT_BY_CATEGORY("Category"),
    SORT_BY_AMOUNT("Amount")
}
