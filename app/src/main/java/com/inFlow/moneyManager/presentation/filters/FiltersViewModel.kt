package com.inFlow.moneyManager.presentation.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.presentation.dashboard.model.*
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiEvent
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiState
import com.inFlow.moneyManager.presentation.shared.FieldError
import com.inFlow.moneyManager.shared.kotlin.FieldType
import com.inFlow.moneyManager.shared.extension.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

// TODO: Move dates timeline error below second date field
@HiltViewModel
class FiltersViewModel @Inject constructor() : ViewModel() {
    private val _stateFlow: MutableStateFlow<FiltersUiState> =
        MutableStateFlow(FiltersUiState.Idle())
    private val stateFlow = _stateFlow.asStateFlow()

    private val eventChannel = Channel<FiltersUiEvent>()
    private val eventFlow = eventChannel.receiveAsFlow()

    val sortOptions = listOf(
        SortBy.SORT_BY_DATE.sortName,
        SortBy.SORT_BY_CATEGORY.sortName,
        SortBy.SORT_BY_AMOUNT.sortName
    )

    val years = listOf(
        LocalDate.now().year.toString(),
        (LocalDate.now().year - 1).toString(),
        (LocalDate.now().year - 2).toString()
    )

    fun setInitialFilters(initialUiModel: FiltersUiModel) {
        updateCurrentUiStateWith {
            FiltersUiState.Idle(initialUiModel)
        }
    }

    fun collectState(coroutineScope: CoroutineScope, callback: (FiltersUiState) -> Unit) {
        coroutineScope.launch {
            stateFlow.collectLatest { callback.invoke(it) }
        }
    }

    fun collectEvents(coroutineScope: CoroutineScope, callback: (FiltersUiEvent) -> Unit) {
        coroutineScope.launch {
            eventFlow.collect { callback.invoke(it) }
        }
    }

    fun onPeriodModeChanged(newPeriodMode: PeriodMode) {
        updateCurrentUiStateWith {
            FiltersUiState.Idle(
                it.copy(periodMode = newPeriodMode, fieldError = null)
            )
        }
    }

    fun onSortByChanged(newSortBy: SortBy) {
        updateCurrentUiStateWith {
            FiltersUiState.Idle(it.copy(sortBy = newSortBy, fieldError = null))
        }
    }

    fun onYearSelected(year: Int) {
        runCatching {
            requireUiState().uiModel
        }.mapCatching {
            it.yearMonth?.month
        }.mapCatching { prevMonth ->
            YearMonth.of(year, prevMonth)
        }.onSuccess { newYearMonth ->
            updateCurrentUiStateWith {
                FiltersUiState.Idle(
                    it.copy(yearMonth = newYearMonth, fieldError = null)
                )
            }
        }.onFailure {
            Timber.e("Failed to select year: $it")
        }
    }

    fun onMonthSelected(monthPosition: Int) {
        runCatching {
            requireUiState().uiModel
        }.mapCatching {
            requireNotNull(it.yearMonth?.year) { "YearMonth cannot be null" }
        }.mapCatching { prevYear ->
            YearMonth.of(prevYear, monthPosition + 1)
        }.onSuccess { newYearMonth ->
            updateCurrentUiStateWith {
                FiltersUiState.Idle(
                    it.copy(yearMonth = newYearMonth, fieldError = null)
                )
            }
        }.onFailure {
            Timber.e("Failed to select month: $it")
        }
    }

    fun onIncomesChecked() {
        runCatching {
            requireUiState().uiModel
        }.mapCatching { uiModel ->
            val newShowTransactionsType = when (uiModel.showTransactions) {
                ShowTransactions.SHOW_EXPENSES -> ShowTransactions.SHOW_BOTH
                ShowTransactions.SHOW_NONE -> ShowTransactions.SHOW_INCOMES
                else -> null
            }
            requireNotNull(newShowTransactionsType) { "Unable to check incomes - invalid previous value" }
        }.onSuccess { newShowTransactionsType ->
            updateCurrentUiStateWith {
                FiltersUiState.Idle(
                    it.copy(showTransactions = newShowTransactionsType, fieldError = null)
                )
            }
        }.onFailure {
            Timber.e("Failed to update show transactions: $it")
        }
    }

    fun onIncomesUnchecked() {
        runCatching {
            requireUiState().uiModel
        }.mapCatching { uiModel ->
            val newShowTransactionsType = when (uiModel.showTransactions) {
                ShowTransactions.SHOW_BOTH -> ShowTransactions.SHOW_EXPENSES
                ShowTransactions.SHOW_INCOMES -> ShowTransactions.SHOW_NONE
                else -> null
            }
            requireNotNull(newShowTransactionsType) { "Unable to check incomes - invalid previous value" }
        }.onSuccess { newShowTransactionsType ->
            updateCurrentUiStateWith {
                FiltersUiState.Idle(
                    it.copy(showTransactions = newShowTransactionsType, fieldError = null)
                )
            }
        }.onFailure {
            Timber.e("Failed to update show transactions: $it")
        }
    }

    fun onExpensesChecked() {
        runCatching {
            requireUiState().uiModel
        }.mapCatching { uiModel ->
            val newShowTransactionsType = when (uiModel.showTransactions) {
                ShowTransactions.SHOW_INCOMES -> ShowTransactions.SHOW_BOTH
                ShowTransactions.SHOW_NONE -> ShowTransactions.SHOW_EXPENSES
                else -> null
            }
            requireNotNull(newShowTransactionsType) { "Unable to check incomes - invalid previous value" }
        }.onSuccess { newShowTransactionsType ->
            updateCurrentUiStateWith {
                FiltersUiState.Idle(
                    it.copy(showTransactions = newShowTransactionsType, fieldError = null)
                )
            }
        }.onFailure {
            Timber.e("Failed to update show transactions: $it")
        }
    }

    fun onExpensesUnchecked() {
        runCatching {
            requireUiState().uiModel
        }.mapCatching { uiModel ->
            val newShowTransactionsType = when (uiModel.showTransactions) {
                ShowTransactions.SHOW_BOTH -> ShowTransactions.SHOW_INCOMES
                ShowTransactions.SHOW_EXPENSES -> ShowTransactions.SHOW_NONE
                else -> null
            }
            requireNotNull(newShowTransactionsType) { "Unable to check incomes - invalid previous value" }
        }.onSuccess { newShowTransactionsType ->
            updateCurrentUiStateWith {
                FiltersUiState.Idle(
                    it.copy(showTransactions = newShowTransactionsType, fieldError = null)
                )
            }
        }.onFailure {
            Timber.e("Failed to update show transactions: $it")
        }
    }

    fun onDescendingSelected() {
        updateCurrentUiStateWith {
            FiltersUiState.Idle(
                it.copy(isDescending = true, fieldError = null)
            )
        }
    }

    fun onAscendingSelected() {
        updateCurrentUiStateWith {
            FiltersUiState.Idle(
                it.copy(isDescending = false, fieldError = null)
            )
        }
    }

    fun onClearClicked() = viewModelScope.launch {
        updateCurrentUiStateWith {
            FiltersUiState.Idle(FiltersUiModel())
        }
    }

    fun onApplyClicked(fromDateString: String, toDateString: String) {
        runCatching {
            requireUiState().uiModel
        }.map {
            it.copy(
                dateFrom = fromDateString.toLocalDate(),
                dateTo = toDateString.toLocalDate()
            )
        }.map { newUiModel ->
            updateCurrentUiStateWith {
                FiltersUiState.Idle(newUiModel)
            }
            newUiModel to newUiModel.validateFilters()
        }.onSuccess { (newUiModel, fieldError) ->
            fieldError.handleError(newUiModel)
        }.onFailure {
            Timber.e("Failed to apply filters: $it")
        }
    }

    /**
     * Returns null if there is no error. Won't validate dates that are after today and will instead return no data
     */
    private fun FiltersUiModel.validateFilters(): FieldError? {
        when {
            showTransactions == ShowTransactions.SHOW_NONE -> return FieldError(
                FieldType.OTHER,
                R.string.error_category_type_not_selected,
            )
            periodMode == PeriodMode.WHOLE_MONTH ->
                yearMonth ?: return FieldError(
                    FieldType.OTHER,
                    R.string.error_invalid_month,
                )
            else -> {
                dateFrom ?: return FieldError(
                    FieldType.DATE_FROM,
                    R.string.error_invalid_date,
                )
                dateTo ?: return FieldError(
                    FieldType.DATE_TO,
                    R.string.error_invalid_date,
                )
                if (dateFrom.isAfter(dateTo)) return FieldError(
                    FieldType.OTHER,
                    R.string.error_invalid_dates,
                )
            }
        }
        return null
    }

    private fun FieldError?.handleError(newUiModel: FiltersUiModel) {
        this?.let { error ->
            updateCurrentUiStateWith {
                FiltersUiState.Error(
                    it.copy(fieldError = error)
                )
            }
        } ?: FiltersUiEvent.NavigateUp(newUiModel).emit()
    }

    private fun updateCurrentUiStateWith(uiStateProvider: (FiltersUiModel) -> FiltersUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun FiltersUiEvent.emit() = viewModelScope.launch {
        eventChannel.send(this@emit)
    }

    private fun requireUiState(): FiltersUiState = stateFlow.value
}
