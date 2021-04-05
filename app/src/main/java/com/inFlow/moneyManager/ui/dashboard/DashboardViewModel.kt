package com.inFlow.moneyManager.ui.dashboard

import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.db.entities.Transaction
import com.inFlow.moneyManager.db.entities.TransactionsDao
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.ui.filters.FieldError
import com.inFlow.moneyManager.ui.filters.FiltersEvent
import com.inFlow.moneyManager.ui.filters.PeriodMode
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
class DashboardViewModel(private val repo: AppRepository) : ViewModel() {
    var activeFilters = MutableStateFlow(FiltersDto())
    val searchQuery = MutableStateFlow("")

    private val dashboardEventChannel = Channel<DashboardEvent>()
    val dashboardEvent = dashboardEventChannel.receiveAsFlow()

    val transactionList = activeFilters.flatMapLatest {
        repo.getAllTransactions()
    }

//    val _balanceFlow = MutableStateFlow<Transaction?>(null)
    val balanceFlow: Flow<Transaction?> = flow {
        emitAll(repo.db.transactionsDao().getMostRecentTransaction())
    }

    val transactions = transactionList.asLiveData()
}

sealed class DashboardEvent {
    data class ShowFiltersDialog(val filtersData: FiltersDto) : DashboardEvent()
}