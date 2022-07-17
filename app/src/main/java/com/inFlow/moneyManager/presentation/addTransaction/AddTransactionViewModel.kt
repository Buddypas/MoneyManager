package com.inFlow.moneyManager.presentation.addTransaction

import androidx.lifecycle.*
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.presentation.addTransaction.extension.updateWith
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiEvent
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiModel
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiState
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import com.inFlow.moneyManager.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    private val _stateFlow: MutableStateFlow<AddTransactionUiState> =
        MutableStateFlow(AddTransactionUiState.Idle())
    private val stateFlow = _stateFlow.asStateFlow()

    private val eventChannel = Channel<AddTransactionUiEvent>()
    private val eventFlow = eventChannel.receiveAsFlow()

    init {
        initData()
    }

    private fun initData() = viewModelScope.launch {
        updateCurrentUiStateWith {
            AddTransactionUiState.LoadingCategories(it)
        }
        awaitAll(loadExpensesAsync(), loadIncomesAsync()).apply {
            updateCurrentUiStateWith {
                AddTransactionUiState.Idle(
                    it.copy(
                        expenseList = get(0),
                        incomeList = get(1),
                        activeCategoryList =
                        if (it.categoryType == CategoryType.EXPENSE) get(0)
                        else get(1)
                    )
                )
            }
        }
    }

    fun collectState(
        viewLifecycleOwner: LifecycleOwner,
        callback: (AddTransactionUiState) -> Unit
    ) {
        viewModelScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stateFlow.collectLatest { callback.invoke(it) }
            }
        }
    }

    fun collectEvents(
        viewLifecycleOwner: LifecycleOwner,
        callback: (AddTransactionUiEvent) -> Unit
    ) {
        viewModelScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventFlow.collectLatest { callback.invoke(it) }
            }
        }
    }

    private fun loadExpensesAsync() = viewModelScope.async {
        repository.getAllExpenseCategories()
    }

    private fun loadIncomesAsync() = viewModelScope.async {
        repository.getAllIncomeCategories()
    }

    fun onTypeClick(isExpenseChecked: Boolean) {
        updateCurrentUiStateWith { uiModel ->
            val newCategoryType =
                if (isExpenseChecked) CategoryType.EXPENSE
                else CategoryType.INCOME
            val newCategoryList =
                if (newCategoryType == CategoryType.EXPENSE)
                    uiModel.expenseList
                else
                    uiModel.incomeList
            requireUiState().updateWith(
                uiModel.copy(
                    selectedCategory = null,
                    categoryType = newCategoryType,
                    activeCategoryList = newCategoryList
                )
            )
        }
    }

    fun onCategoryClick(position: Int) {
        updateCurrentUiStateWith { currentUiModel ->
            val newCategory =
                if (currentUiModel.categoryType == CategoryType.EXPENSE)
                    currentUiModel.expenseList?.getOrNull(position)
                else currentUiModel.incomeList?.getOrNull(position)
            requireUiState().updateWith(currentUiModel.copy(selectedCategory = newCategory))
        }
    }

    fun onSaveClick(description: String?, amount: Double?) {
        requireUiState().uiModel.let { uiModel ->
            when {
                uiModel.selectedCategory == null -> updateCurrentUiStateWith {
                    AddTransactionUiState.Error(
                        uiModel.copy(categoryErrorResId = R.string.error_category_not_selected)
                    )
                }
                description.isNullOrBlank() -> updateCurrentUiStateWith {
                    AddTransactionUiState.Error(
                        uiModel.copy(descriptionErrorResId = R.string.error_empty_description)
                    )
                }
                amount == null || amount <= 0.0 -> updateCurrentUiStateWith {
                    AddTransactionUiState.Error(
                        uiModel.copy(amountErrorResId = R.string.error_amount_must_be_positive)
                    )
                }
                else -> saveTransaction(description, amount)
            }
        }
    }

    private fun saveTransaction(desc: String, amount: Double) {
        viewModelScope.launch {
            runCatching {
                requireUiState().uiModel
            }.mapCatching { uiModel ->
                val realAmount =
                    if (uiModel.categoryType == CategoryType.EXPENSE) -amount
                    else amount
                val categoryId = uiModel.selectedCategory!!.categoryId
                realAmount to categoryId
            }.mapCatching { pair ->
                pair.first to requireNotNull(pair.second) { "categoryId must not be null" }
            }.onSuccess { pair ->
                withContext(Dispatchers.IO) {
                    repository.saveTransaction(pair.first, pair.second, desc)
                }
                withContext(Dispatchers.Main) {
                    showSuccess("Transaction added.")
                    navigateUp()
                }
            }.onFailure {
                Timber.e("Failed to save transaction: $it")
            }
        }
    }

    private suspend fun showSuccess(msg: String) {
        eventChannel.send(AddTransactionUiEvent.ShowSuccessMessage(msg))
    }

    private suspend fun navigateUp() {
        eventChannel.send(AddTransactionUiEvent.NavigateUp)
    }

    private fun updateCurrentUiStateWith(uiStateProvider: (AddTransactionUiModel) -> AddTransactionUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun requireUiState(): AddTransactionUiState = stateFlow.value
}