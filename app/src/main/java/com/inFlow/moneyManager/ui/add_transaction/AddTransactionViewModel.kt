package com.inFlow.moneyManager.ui.add_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.shared.kotlin.setValueIfDifferent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddTransactionViewModel(private val repository: AppRepository) : ViewModel() {
    private val _categoryType = MutableStateFlow(CategoryType.EXPENSE)
    val categoryType: StateFlow<CategoryType> by this::_categoryType

    private val _expenseFlow = MutableStateFlow(listOf<Category>())
    val expenseFlow: StateFlow<List<Category>> by this::_expenseFlow

    private val _incomeFlow = MutableStateFlow(listOf<Category>())
    val incomeFlow: StateFlow<List<Category>> by this::_incomeFlow

    val expenses = expenseFlow.asLiveData()
    val incomes = incomeFlow.asLiveData()

    init {
        loadExpenses()
        loadIncomes()
    }

    fun onTypeSelected(checkedId: Int) {
        if (checkedId == R.id.expense_btn)
            _categoryType.setValueIfDifferent(CategoryType.EXPENSE)
        else
            _categoryType.setValueIfDifferent(CategoryType.INCOME)
    }

    fun loadExpenses() = viewModelScope.launch {
        _expenseFlow.emitAll(repository.getAllExpenseCategories())
    }

    fun loadIncomes() = viewModelScope.launch {
        _incomeFlow.emitAll(repository.getAllIncomeCategories())
    }


}

enum class CategoryType {
    EXPENSE, INCOME;
}