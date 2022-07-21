package com.inFlow.moneyManager.presentation.dashboard

import androidx.lifecycle.*
import com.inFlow.moneyManager.data.repository.TransactionRepository
import com.inFlow.moneyManager.presentation.dashboard.model.DashboardUiEvent
import com.inFlow.moneyManager.presentation.dashboard.model.DashboardUiModel
import com.inFlow.moneyManager.presentation.dashboard.model.DashboardUiState
import com.inFlow.moneyManager.presentation.dashboard.model.Filters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DashboardViewModel @Inject constructor(private val repository: TransactionRepository) :
    ViewModel() {

    private val _stateFlow = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading())
    private val stateFlow = _stateFlow.asStateFlow()

    var activeFilters = MutableStateFlow(Filters())
    val query = MutableStateFlow("")

    private val eventChannel = Channel<DashboardUiEvent>(Channel.BUFFERED)
    val eventFlow = eventChannel.receiveAsFlow()

    private val _balanceData = MutableStateFlow<Pair<Double, Double>?>(null)
    val balanceData: StateFlow<Pair<Double, Double>?> by this::_balanceData

    val transactionList = combine(activeFilters, query) { filters, query ->
        Pair(filters, query)
    }.flatMapLatest {
        repository.getTransactions(it.first, it.second)
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

    fun fetchBalanceData() = viewModelScope.launch {
        val expenseList = repository.getAllExpenses()
        var expenses = 0.0
        if (expenseList.isNotEmpty()) expenseList.forEach {
            expenses -= it.transactionAmount
        }

        val incomeList = repository.getAllIncomes()
        var incomes = 0.0
        if (incomeList.isNotEmpty()) incomeList.forEach {
            incomes += it.transactionAmount
        }
        _balanceData.value = Pair(incomes, expenses)
    }

    fun onAddClicked() = viewModelScope.launch {
        eventChannel.send(DashboardUiEvent.NavigateToAddTransaction)
    }

    fun openFilters() = viewModelScope.launch {
        eventChannel.send(DashboardUiEvent.OpenFilters(activeFilters.value))
    }

    private fun updateCurrentUiStateWith(uiStateProvider: (DashboardUiModel) -> DashboardUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun requireUiState(): DashboardUiState = stateFlow.value
}
