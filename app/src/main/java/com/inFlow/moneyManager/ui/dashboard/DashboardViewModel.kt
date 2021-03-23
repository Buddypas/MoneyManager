package com.inFlow.moneyManager.ui.dashboard

import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.ui.filters.FieldError
import com.inFlow.moneyManager.ui.filters.FiltersEvent
import com.inFlow.moneyManager.ui.filters.PeriodMode
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class DashboardViewModel(private val repo: AppRepository) : ViewModel() {
    var activeFilters = MutableStateFlow(FiltersDto())
    val searchQuery = MutableStateFlow("")

    private val dashboardEventChannel = Channel<DashboardEvent>()
    val dashboardEvent = dashboardEventChannel.receiveAsFlow()

    fun populateDb() = repo.populateDb(viewModelScope)

    @ExperimentalCoroutinesApi
    val transactionList = activeFilters.flatMapLatest {
        repo.getAllTransactions()
    }
}

sealed class DashboardEvent {
    data class ShowFiltersDialog(val filtersData: FiltersDto) : DashboardEvent()
}