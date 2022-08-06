package com.inFlow.moneyManager.presentation.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiState
import com.inFlow.moneyManager.presentation.dashboard.model.*
import com.inFlow.moneyManager.presentation.filters.extension.updateWith
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiEvent
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiState
import com.inFlow.moneyManager.shared.kotlin.FieldType
import com.inFlow.moneyManager.shared.kotlin.toLocalDate
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

@HiltViewModel
class FiltersViewModel @Inject constructor() : ViewModel() {
    private val _stateFlow: MutableStateFlow<FiltersUiState> =
        MutableStateFlow(FiltersUiState.Idle())
    private val stateFlow = _stateFlow.asStateFlow()

    private val eventChannel = Channel<FiltersUiEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

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

    fun onFromDateChanged(dateString: String) {
        runCatching {
            requireUiState().uiModel
        }.onSuccess {
            updateCurrentUiStateWith {
                requireUiState().updateWith(it.copy(dateFrom = dateString.toLocalDate()))
            }
        }.onFailure {
            Timber.e("Failed to update period mode: $it")
        }
    }

    fun onPeriodModeChanged(newPeriodMode: PeriodMode) {
        runCatching {
            requireUiState().uiModel
        }.onSuccess {
            updateCurrentUiStateWith {
                requireUiState().updateWith(it.copy(periodMode = newPeriodMode))
            }
        }.onFailure {
            Timber.e("Failed to update period mode: $it")
        }
    }

    fun onSortByChanged(newSortBy: SortBy) {
        runCatching {
            requireUiState().uiModel
        }.onSuccess {
            updateCurrentUiStateWith {
                requireUiState().updateWith(it.copy(sortBy = newSortBy))
            }
        }.onFailure {
            Timber.e("Failed to update period mode: $it")
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
                requireUiState().updateWith(
                    it.copy(yearMonth = newYearMonth)
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
                requireUiState().updateWith(
                    it.copy(yearMonth = newYearMonth)
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
                requireUiState().updateWith(
                    it.copy(showTransactions = newShowTransactionsType)
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
                requireUiState().updateWith(
                    it.copy(showTransactions = newShowTransactionsType)
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
                requireUiState().updateWith(
                    it.copy(showTransactions = newShowTransactionsType)
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
                requireUiState().updateWith(
                    it.copy(showTransactions = newShowTransactionsType)
                )
            }
        }.onFailure {
            Timber.e("Failed to update show transactions: $it")
        }
    }

    fun onDescendingSelected() {
        runCatching {
            requireUiState().uiModel
        }.onSuccess { uiModel ->
            updateCurrentUiStateWith {
                requireUiState().updateWith(
                    it.copy(isDescending = true)
                )
            }
        }.onFailure {
            Timber.e("Failed to update sort order: $it")
        }
    }

    fun onAscendingSelected() {
        runCatching {
            requireUiState().uiModel
        }.onSuccess { uiModel ->
            updateCurrentUiStateWith {
                requireUiState().updateWith(
                    it.copy(isDescending = false)
                )
            }
        }.onFailure {
            Timber.e("Failed to update sort order: $it")
        }
    }

    /**
     * Returns null if there is no error. Won't validate dates that are after today and will instead return no data
     */
    private fun validateFilters(): FieldError? {
        runCatching {
            requireUiState().uiModel
        }.map { uiModel ->
            when {
                uiModel.showTransactions == ShowTransactions.SHOW_NONE -> return@validateFilters FieldError(
                    "Incomes or expenses must be selected",
                    FieldType.FIELD_OTHER
                )
                uiModel.periodMode == PeriodMode.WHOLE_MONTH && uiModel.yearMonth == null ->
                    return@validateFilters FieldError(
                        "Month is not valid",
                        FieldType.FIELD_OTHER
                    )
                else -> {
                    uiModel.dateFrom ?: return FieldError(
                        "Date is not valid",
                        FieldType.FIELD_DATE_FROM
                    )
                    uiModel.dateTo ?: return FieldError(
                        "Date is not valid",
                        FieldType.FIELD_DATE_TO
                    )
                    if(uiModel.dateFrom.isAfter(uiModel.dateTo)) return@validateFilters FieldError(
                        "Dates are not valid",
                        FieldType.FIELD_OTHER
                    )
                }
            }
        }
        return null
    }

    fun onApplyClicked() = viewModelScope.launch {
        validateFilters()?.let { error ->
            updateCurrentUiStateWith {
                FiltersUiState.Error(
                    it.copy(fieldError = error)
                )
            }
        }
//            ?: Filters(
//            period = filters.period,
//            showTransactionsOfType = filters.showTransactionsOfType,
//            yearMonth =
//            if (filters.period == PeriodMode.WHOLE_MONTH) filters.yearMonth
//            else null,
//            customRange =
//            if (filters.period == PeriodMode.CUSTOM_RANGE) filters.customRange
//            else Pair(null, null),
//            sortBy = filters.sortBy,
//            isDescending = filters.isDescending
//        ).also {
//            eventChannel.send(FiltersUiEvent.ApplyFilters(it))
//        }
    }

    fun onClearClicked() = viewModelScope.launch {
        updateCurrentUiStateWith {
            FiltersUiState.Idle(FiltersUiModel())
        }
    }

    private fun updateCurrentUiStateWith(uiStateProvider: (FiltersUiModel) -> FiltersUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun FiltersUiEvent.emit() = viewModelScope.launch {
        eventChannel.send(this@emit)
    }

    private fun requireUiState(): FiltersUiState = stateFlow.value
}
