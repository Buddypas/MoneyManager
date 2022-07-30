package com.inFlow.moneyManager.presentation.addTransaction

import androidx.lifecycle.*
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.data.db.entities.CategoryDto
import com.inFlow.moneyManager.data.repository.CategoryRepositoryImpl
import com.inFlow.moneyManager.data.repository.TransactionRepositoryImpl
import com.inFlow.moneyManager.presentation.addCategory.model.Categories
import com.inFlow.moneyManager.presentation.addCategory.model.FieldError
import com.inFlow.moneyManager.presentation.addCategory.model.FieldType
import com.inFlow.moneyManager.presentation.addTransaction.extension.inferCategoryType
import com.inFlow.moneyManager.presentation.addTransaction.extension.updateCategoryType
import com.inFlow.moneyManager.presentation.addTransaction.extension.updateWith
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiEvent
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiModel
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiState
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
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
class AddTransactionViewModel @Inject constructor(private val transactionRepository: TransactionRepositoryImpl, private val categoryRepository: CategoryRepositoryImpl) :
    ViewModel() {

    private val _stateFlow = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Idle())
    private val stateFlow = _stateFlow.asStateFlow()

    private val eventChannel = Channel<AddTransactionUiEvent>()
    private val eventFlow = eventChannel.receiveAsFlow()

    init {
        initData()
    }

    fun collectState(
        viewLifecycleOwner: LifecycleOwner,
        callback: (AddTransactionUiState) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stateFlow.collectLatest { callback.invoke(it) }
            }
        }
    }

    fun collectEvents(
        viewLifecycleOwner: LifecycleOwner,
        callback: (AddTransactionUiEvent) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventFlow.collectLatest { callback.invoke(it) }
            }
        }
    }

    fun onTypeClick(isExpenseChecked: Boolean) {
        updateCurrentUiStateWith { uiModel ->
            requireUiState().updateWith(
                uiModel.updateCategoryType(isExpenseChecked)
            )
        }
    }

    fun onCategoryClick(position: Int) {
        requireUiState().uiModel.runCatching {
            if (categoryType == CategoryType.EXPENSE)
                expenseList?.getOrNull(position)
            else incomeList?.getOrNull(position)
        }.mapCatching {
            requireNotNull(it) { "Selected category cannot be null" }
        }.onSuccess { newCategory ->
            updateCurrentUiStateWith {
                requireUiState().updateWith(it.copy(selectedCategory = newCategory))
            }
        }
    }

    // TODO: Remove !!
    fun onSaveClick(description: String?, amount: Double?) {
        requireUiState().uiModel.let { uiModel ->
            isTransactionValid(uiModel.selectedCategory, description, amount)?.let { fieldError ->
                updateCurrentUiStateWith {
                    AddTransactionUiState.Error(fieldError.toErrorUiModel(it))
                }
            } ?: saveTransaction(description!!, amount!!)
        }
    }

    // TODO: Make mapper
    private fun isTransactionValid(
        selectedCategory: CategoryDto?,
        description: String?,
        amount: Double?
    ): FieldError? = when {
        selectedCategory == null -> FieldError(
            FieldType.CATEGORY,
            R.string.error_category_not_selected
        )
        description.isNullOrBlank() -> FieldError(
            FieldType.DESCRIPTION,
            R.string.error_empty_description
        )
        amount == null || amount <= 0.0 -> FieldError(
            FieldType.AMOUNT,
            R.string.error_amount_must_be_positive
        )
        else -> null
    }

    private fun initData() = viewModelScope.launch {
        updateCurrentUiStateWith {
            AddTransactionUiState.LoadingCategories(it)
        }
        runCatching {
            fetchCategories()
        }.onSuccess { categories ->
            updateCurrentUiStateWith {
                AddTransactionUiState.Idle(
                    it.copy(
                        expenseList = categories.expenses,
                        incomeList = categories.incomes,
                        activeCategoryList = it.inferCategoryType(categories)
                    )
                )
            }
        }.onFailure {
            Timber.e("Unable to fetch categories: $it")
        }
    }

    private suspend fun fetchCategories(): Categories {
        val expenses = viewModelScope.async { categoryRepository.getAllExpenseCategories() }
        val incomes = viewModelScope.async { categoryRepository.getAllIncomeCategories() }
        return Categories(expenses.await(), incomes.await())
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
                // TODO: Check if save successful
                transactionRepository.saveTransaction(pair.first, pair.second, desc)
                AddTransactionUiEvent.ShowSuccessMessage("TransactionDto added.").emit()
                AddTransactionUiEvent.NavigateUp.emit()
            }.onFailure {
                Timber.e("Failed to save transaction: $it")
            }
        }
    }

    private fun AddTransactionUiEvent.emit() = viewModelScope.launch {
        eventChannel.send(this@emit)
    }

    private fun updateCurrentUiStateWith(uiStateProvider: (AddTransactionUiModel) -> AddTransactionUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun requireUiState(): AddTransactionUiState = stateFlow.value

    private fun FieldError.toErrorUiModel(currentUiModel: AddTransactionUiModel) =
        when (fieldType) {
            FieldType.CATEGORY -> currentUiModel.copy(categoryErrorResId = errorResId)
            FieldType.DESCRIPTION -> currentUiModel.copy(descriptionErrorResId = errorResId)
            FieldType.AMOUNT -> currentUiModel.copy(amountErrorResId = errorResId)
        }
}
