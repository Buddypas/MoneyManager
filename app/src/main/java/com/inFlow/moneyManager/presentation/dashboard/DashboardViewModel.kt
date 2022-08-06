package com.inFlow.moneyManager.presentation.dashboard

import androidx.lifecycle.*
import com.inFlow.moneyManager.domain.transaction.model.Transaction
import com.inFlow.moneyManager.domain.transaction.usecase.GetExpensesAndIncomesUseCase
import com.inFlow.moneyManager.domain.transaction.usecase.GetTransactionsUseCase
import com.inFlow.moneyManager.presentation.dashboard.extensions.updateWith
import com.inFlow.moneyManager.presentation.dashboard.model.*
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

private const val QUERY_DEBOUNCE_DURATION = 1500L

// TODO: Fetch entire balance data from db instead of incomes and expenses separately
@ExperimentalCoroutinesApi
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getExpensesAndIncomesUseCase: GetExpensesAndIncomesUseCase
) :
    ViewModel() {

    private val _stateFlow = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading())
    private val stateFlow = _stateFlow.asStateFlow()
    private val eventChannel = Channel<DashboardUiEvent>(Channel.BUFFERED)
    private val eventFlow = eventChannel.receiveAsFlow()
    private var queryUpdateJob: Job? = null

    fun collectState(
        coroutineScope: CoroutineScope,
        callback: (DashboardUiState) -> Unit
    ) {
        coroutineScope.launch {
            stateFlow.collectLatest { callback.invoke(it) }
        }
    }

    fun collectEvents(
        coroutineScope: CoroutineScope,
        callback: (DashboardUiEvent) -> Unit
    ) {
        coroutineScope.launch {
            eventFlow.collect { callback.invoke(it) }
        }
    }

    fun onAddClicked() {
        DashboardUiEvent.NavigateToAddTransaction.emitEvent()
    }

    fun openFilters() {
        DashboardUiEvent.OpenFilters(requireUiState().uiModel.filters).emitEvent()
    }

    fun updateQuery(query: String) {
        queryUpdateJob?.cancel()
        queryUpdateJob = viewModelScope.launch {
            delay(QUERY_DEBOUNCE_DURATION)
            updateCurrentUiStateWith {
                requireUiState().updateWith(it.copy(query = query))
            }
            updateTransactionListAndBalance()
        }
    }

    fun updateFilters(filters: FiltersUiModel) {
        updateCurrentUiStateWith {
            requireUiState().updateWith(it.copy(filters = filters))
        }
        updateTransactionListAndBalance()
    }

    fun updateTransactionListAndBalance() {
        viewModelScope.launch {
            runCatching {
                fetchTransactionListAsync() to fetchBalanceDataAsync()
            }.mapCatching {
                it.first.await() to it.second.await()
            }.onSuccess { (transactionList, balanceData) ->
                updateCurrentUiStateWith {
                    DashboardUiState.Idle(
                        it.copy(
                            income = balanceData.second,
                            expenses = balanceData.first,
                            transactionList = transactionList
                        )
                    )
                }
            }.onFailure {
                Timber.e("updateTransactionListAndBalance: $it")
                // TODO: Create error state
            }
        }
    }

    private fun CoroutineScope.fetchBalanceDataAsync(): Deferred<Pair<Double, Double>> =
        async { getExpensesAndIncomesUseCase.execute() }

    private fun CoroutineScope.fetchTransactionListAsync(): Deferred<List<Transaction>> =
        async {
            runCatching {
                requireUiState().uiModel
            }.mapCatching { uiModel ->
                getTransactionsUseCase.execute(uiModel.filters to uiModel.query)
            }.getOrThrow()
        }

    private fun DashboardUiEvent.emitEvent() {
        viewModelScope.launch {
            eventChannel.send(this@emitEvent)
        }
    }

    // TODO: Figure out way to not use requireUiState when updating
    private fun updateCurrentUiStateWith(uiStateProvider: (DashboardUiModel) -> DashboardUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun requireUiState(): DashboardUiState = stateFlow.value
}
