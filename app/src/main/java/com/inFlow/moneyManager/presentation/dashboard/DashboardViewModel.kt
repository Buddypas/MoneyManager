package com.inFlow.moneyManager.presentation.dashboard

import androidx.lifecycle.*
import com.inFlow.moneyManager.data.db.entities.TransactionDto
import com.inFlow.moneyManager.data.repository.TransactionRepositoryImpl
import com.inFlow.moneyManager.presentation.dashboard.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

private const val QUERY_DEBOUNCE_DURATION = 2000L

// TODO: Prepopulate database with categories and transactions
// TODO: Fetch entire balance data from db instead of incomes and expenses separately
@ExperimentalCoroutinesApi
@HiltViewModel
class DashboardViewModel @Inject constructor(private val repository: TransactionRepositoryImpl) :
    ViewModel() {

    private val _stateFlow = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading())
    private val stateFlow = _stateFlow.asStateFlow()
    private val eventChannel = Channel<DashboardUiEvent>(Channel.BUFFERED)
    private val eventFlow = eventChannel.receiveAsFlow()
    private var queryUpdateJob: Job? = null

    init {
        updateTransactionListAndBalance()
    }

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
            eventFlow.collectLatest { callback.invoke(it) }
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

    fun updateFilters(filters: Filters) {
        updateCurrentUiStateWith {
            requireUiState().updateWith(it.copy(filters = filters))
        }
        updateTransactionListAndBalance()
    }

    private fun updateTransactionListAndBalance() {
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

    // TODO: Move asyncs to repository
    private fun CoroutineScope.fetchBalanceDataAsync(): Deferred<Pair<Double, Double>> =
        async {
            runCatching {
                async { repository.calculateExpenses() } to async { repository.calculateIncomes() }
            }.mapCatching {
                awaitAll(it.first, it.second)
            }.map {
                it[0] to it[1]
            }.getOrThrow()
        }

    private fun CoroutineScope.fetchTransactionListAsync(): Deferred<List<TransactionDto>> =
        async {
            runCatching {
                requireUiState().uiModel
            }.mapCatching {
                repository.getTransactions(it.filters, it.query)
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
