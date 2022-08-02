package com.inFlow.moneyManager.presentation.dashboard

import androidx.lifecycle.*
import com.inFlow.moneyManager.data.repository.TransactionRepositoryImpl
import com.inFlow.moneyManager.presentation.dashboard.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val QUERY_DEBOUNCE_DURATION = 2000L

// TODO: Pass coroutine scope instead of viewLifecycleOwner
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
        updateTransactionList()
    }

    fun collectState(
        viewLifecycleOwner: LifecycleOwner,
        callback: (DashboardUiState) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stateFlow.collectLatest { callback.invoke(it) }
            }
        }
    }

    fun collectEvents(
        viewLifecycleOwner: LifecycleOwner,
        callback: (DashboardUiEvent) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventFlow.collectLatest { callback.invoke(it) }
            }
        }
    }

    private fun fetchBalanceData() {
        viewModelScope.launch {
            var expenses = 0.0
            var incomes = 0.0
            val expenseList = repository.getAllExpenses()
            val incomeList = repository.getAllIncomes()
            if (expenseList.isNotEmpty()) expenseList.forEach {
                expenses -= it.transactionAmount
            }
            if (incomeList.isNotEmpty()) incomeList.forEach {
                incomes += it.transactionAmount
            }
            updateCurrentUiStateWith {
                DashboardUiState.Idle(
                    it.copy(income = incomes, expenses = expenses)
                )
            }
        }
    }

    fun onAddClicked() {
        viewModelScope.launch {
            eventChannel.send(DashboardUiEvent.NavigateToAddTransaction)
        }
    }

    fun openFilters() {
        viewModelScope.launch {
            eventChannel.send(DashboardUiEvent.OpenFilters(requireUiState().uiModel.filters))
        }
    }

    fun updateQuery(query: String) {
        queryUpdateJob?.cancel()
        queryUpdateJob = viewModelScope.launch {
            delay(QUERY_DEBOUNCE_DURATION)
            updateCurrentUiStateWith {
                requireUiState().updateWith(it.copy(query = query))
            }
            updateTransactionList()
        }
    }

    fun updateFilters(filters: Filters) {
        updateCurrentUiStateWith {
            requireUiState().updateWith(it.copy(filters = filters))
        }
        updateTransactionList()
    }

    private fun updateTransactionList() {
        viewModelScope.launch {
            runCatching {
                requireUiState().uiModel
            }.mapCatching {
                repository.getTransactions(it.filters, it.query)
            }.onSuccess { transactionList ->
                updateCurrentUiStateWith {
                    requireUiState().updateWith(it.copy(transactionList = transactionList))
                }
                // TODO: Run these two in parallel in the init block
                fetchBalanceData()
            }.onFailure {
                Timber.e("failed to update transactions: $it")
                // TODO: Create error state
            }
        }
    }

    // TODO: Figure out way to not use requireUiState when updating
    private fun updateCurrentUiStateWith(uiStateProvider: (DashboardUiModel) -> DashboardUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun requireUiState(): DashboardUiState = stateFlow.value
}
