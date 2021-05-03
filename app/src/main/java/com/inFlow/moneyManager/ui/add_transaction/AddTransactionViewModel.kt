package com.inFlow.moneyManager.ui.add_transaction

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddTransactionViewModel(private val repository: AppRepository) : ViewModel() {

    val eventChannel = Channel<AddTransactionEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    private val _expenseFlow = MutableStateFlow<List<Category>>(listOf<Category>())
    val expenseFlow: StateFlow<List<Category>> by this::_expenseFlow

    private val _incomeFlow = MutableStateFlow<List<Category>>(listOf<Category>())
    val incomeFlow: StateFlow<List<Category>> by this::_incomeFlow

    var categoryType = CategoryType.EXPENSE
    var selectedCategoryPosition = -1

    val expenses = expenseFlow.asLiveData()
    val incomes = incomeFlow.asLiveData()

//    val categoriesReady = combine(expenseFlow, incomeFlow) { expenseCategories, incomeCategories ->
//        !(expenseCategories == null || incomeCategories == null)
//    }

    private val _categoriesReady = MutableStateFlow(false)
    val categoriesReady: StateFlow<Boolean> by this::_categoriesReady

//    val categoriesReady = MediatorLiveData<Boolean>()
//
//    init {
//        categoriesReady.value = false
//        categoriesReady.addSource(expenses) {
//            categoriesReady.value = it.isNotEmpty() && !incomes.value.isNullOrEmpty()
//        }
//        categoriesReady.addSource(incomes) {
//            categoriesReady.value = it.isNotEmpty() && !expenses.value.isNullOrEmpty()
//        }
//    }

    fun initData() = viewModelScope.launch {
        loadExpenses()
        loadIncomes()
    }

    private fun loadExpenses() = viewModelScope.launch {
        _expenseFlow.emitAll(repository.getAllExpenseCategories())
    }

    private fun loadIncomes() = viewModelScope.launch {
        _incomeFlow.emitAll(repository.getAllIncomeCategories())
    }

    fun saveTransaction(desc: String, amount: Double) = viewModelScope.launch(Dispatchers.IO) {
        val realAmount = if (categoryType == CategoryType.EXPENSE) -amount else amount
        val catId =
            if (categoryType == CategoryType.EXPENSE) expenses.value!![selectedCategoryPosition].categoryId
            else incomes.value!![selectedCategoryPosition].categoryId
        repository.saveTransaction(realAmount, catId, desc)
        showSuccess("Transaction added.")
        navigateUp()
    }

    private suspend fun showSuccess(msg: String) {
        eventChannel.send(AddTransactionEvent.ShowSuccessMessage(msg))
    }

    private suspend fun navigateUp() {
        eventChannel.send(AddTransactionEvent.NavigateUp)
    }
}

enum class CategoryType {
    EXPENSE, INCOME;
}

sealed class AddTransactionEvent {
    data class ShowErrorMessage(val msg: String?) : AddTransactionEvent()
    data class ShowSuccessMessage(val msg: String) : AddTransactionEvent()
    object NavigateUp : AddTransactionEvent()
}