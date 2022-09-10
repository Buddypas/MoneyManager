package com.inFlow.moneyManager.presentation.addTransaction

import androidx.lifecycle.*
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.domain.category.usecase.GetExpenseCategoriesUseCase
import com.inFlow.moneyManager.domain.category.usecase.GetIncomeCategoriesUseCase
import com.inFlow.moneyManager.domain.transaction.model.Transaction
import com.inFlow.moneyManager.domain.transaction.usecase.SaveTransactionUseCase
import com.inFlow.moneyManager.presentation.addCategory.model.Categories
import com.inFlow.moneyManager.presentation.addTransaction.extension.inferCategoryType
import com.inFlow.moneyManager.presentation.addTransaction.extension.updateCategoryType
import com.inFlow.moneyManager.presentation.addTransaction.extension.updateWith
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiEvent
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiModel
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiState
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import com.inFlow.moneyManager.presentation.shared.FieldError
import com.inFlow.moneyManager.shared.kotlin.FieldType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val getIncomeCategoriesUseCase: GetIncomeCategoriesUseCase,
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Idle())
    private val stateFlow = _stateFlow.asStateFlow()

    private val eventChannel = Channel<AddTransactionUiEvent>()
    private val eventFlow = eventChannel.receiveAsFlow()

    init {
        initData()
    }

    fun collectState(
        coroutineScope: CoroutineScope,
        callback: (AddTransactionUiState) -> Unit
    ) {
        coroutineScope.launch {
            stateFlow.collectLatest { callback.invoke(it) }
        }
    }

    fun collectEvents(
        coroutineScope: CoroutineScope,
        callback: (AddTransactionUiEvent) -> Unit
    ) {
        coroutineScope.launch {
            eventFlow.collect { callback.invoke(it) }
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
            if (categoryType == CategoryType.EXPENSE) {
                expenseList?.getOrNull(position)
            } else incomeList?.getOrNull(position)
        }.mapCatching {
            requireNotNull(it) { "Selected category cannot be null" }
        }.onSuccess { newCategory ->
            updateCurrentUiStateWith {
                requireUiState().updateWith(it.copy(selectedCategory = newCategory))
            }
        }
    }

    fun onSaveClick(description: String?, amount: Double?, date: LocalDate?) {
        updateCurrentUiStateWith {
            requireUiState().updateWith(
                it.copy(
                    selectedDescription = description,
                    selectedAmount = amount,
                    selectedDate = date
                )
            )
        }
        // TODO: Handle state not being updated when error is the same as before
        requireUiState().uiModel.isTransactionValid()?.let { fieldError ->
            updateCurrentUiStateWith {
                AddTransactionUiState.Error(it.copy(fieldError = fieldError))
            }
        } ?: saveTransaction()
    }

    // TODO: Make mapper
    private fun AddTransactionUiModel.isTransactionValid(): FieldError? = when {
        selectedCategory == null -> FieldError(
            FieldType.CATEGORY,
            R.string.error_category_not_selected
        )
        selectedDescription.isNullOrBlank() -> FieldError(
            FieldType.DESCRIPTION,
            R.string.error_empty_description
        )
        selectedAmount == null || selectedAmount <= 0.0 -> FieldError(
            FieldType.AMOUNT,
            R.string.error_amount_must_be_positive
        )
        selectedDate == null || selectedDate.isAfter(LocalDate.now()) -> FieldError(
            FieldType.DATE,
            R.string.error_invalid_date
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
        val expenses = viewModelScope.async { getExpenseCategoriesUseCase.execute() }
        val incomes = viewModelScope.async { getIncomeCategoriesUseCase.execute() }
        return Categories(expenses.await(), incomes.await())
    }

    private fun saveTransaction() {
        viewModelScope.launch {
            runCatching {
                requireUiState().uiModel
            }.mapCatching { uiModel ->
                Transaction(
                    categoryId = requireNotNull(uiModel.selectedCategory?.id) { "categoryId must not be null" },
                    amount = uiModel.selectedAmount.getRealAmount(uiModel.categoryType),
                    description = uiModel.selectedDescription.orEmpty(),
                    date = uiModel.selectedDate
                )
            }.onSuccess { transaction ->
                // TODO: Check if save successful
                saveTransactionUseCase.execute(transaction)
                AddTransactionUiEvent.ShowSuccessMessage(R.string.transaction_added).emit()
                AddTransactionUiEvent.NavigateUp.emit()
            }
        }
    }

    // TODO: Move this to other layers
    private fun Double?.getRealAmount(categoryType: CategoryType): Double =
        runCatching {
            requireNotNull(this) { "Amount cannot be null" }
        }.mapCatching {
            require(it > 0) { "Amount must be larger than 0" }
            it
        }.mapCatching { amount ->
            val realAmount =
                if (categoryType == CategoryType.EXPENSE) -amount
                else amount
            realAmount
        }.getOrThrow()

    private fun AddTransactionUiEvent.emit() = viewModelScope.launch {
        eventChannel.send(this@emit)
    }

    private fun updateCurrentUiStateWith(uiStateProvider: (AddTransactionUiModel) -> AddTransactionUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun requireUiState(): AddTransactionUiState = stateFlow.value
}
