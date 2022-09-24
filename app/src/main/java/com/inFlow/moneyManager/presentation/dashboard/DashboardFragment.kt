package com.inFlow.moneyManager.presentation.dashboard

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding
import com.inFlow.moneyManager.presentation.dashboard.adapter.TransactionsAdapter
import com.inFlow.moneyManager.presentation.dashboard.extensions.*
import com.inFlow.moneyManager.presentation.dashboard.model.*
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel
import com.inFlow.moneyManager.shared.extension.safeNavigate
import com.inFlow.moneyManager.shared.kotlin.KEY_FILTERS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

// TODO: Think about using a subgraph for dashboard and filters and share a view model
// TODO: Add date for transactions
// TODO: Add progress bar
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private var transactionsAdapter: TransactionsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentDashboardBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNavObserver()
        binding.setUpUi()
        handleEvents()
        handleState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateTransactionListAndBalance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectEvents(this) { event ->
                    when (event) {
                        DashboardUiEvent.NavigateToAddTransaction ->
                            findNavController().navigate(
                                DashboardFragmentDirections.actionDashboardToAddTransaction()
                            )
                        is DashboardUiEvent.OpenFilters ->
                            findNavController().safeNavigate(
                                DashboardFragmentDirections.actionDashboardToFilters(event.filters)
                            )
                    }
                }
            }
        }
    }

    private fun handleState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectState(this) { state ->
                    when (state) {
                        is DashboardUiState.Idle -> binding.bindIdle(state)
                        is DashboardUiState.Loading -> Unit
                    }
                }
            }
        }
    }

    private fun FragmentDashboardBinding.setUpUi() {
        toolbar.setUpToolbar()
        transactionsAdapter = TransactionsAdapter().also {
            recyclerTransactions.adapter = it
        }
        buttonAdd.setOnClickListener { viewModel.onAddClicked() }
    }

    private fun MaterialToolbar.setUpToolbar() {
        (menu.findItem(R.id.action_search).actionView as? SearchView)?.setUpSearchView()
        setOnMenuItemClickListener { item ->
            item.takeIf { it.itemId == R.id.action_filter }?.run {
                viewModel.openFilters()
                true
            } ?: false
        }
    }

    private fun SearchView.setUpSearchView() {
        getSearchText().setTextColor(Color.WHITE)
        getCloseButton().imageTintList = ColorStateList.valueOf(Color.WHITE)
        setQueryChangedListener { viewModel.updateQuery(it) }
    }

    private fun FragmentDashboardBinding.bindIdle(state: DashboardUiState.Idle) {
        textMonth.text =
            if (state.uiModel.filters.periodMode == PeriodMode.WHOLE_MONTH) {
                state.uiModel.filters.yearMonth?.month?.name
            } else getString(R.string.custom_range)
        textFilters.text = state.uiModel.filters.toFiltersString()
        updateBalanceData(state.uiModel.income, state.uiModel.expenses)
        textNoTransactions.isVisible = state.uiModel.transactionList.isNullOrEmpty()
        transactionsAdapter?.submitList(state.uiModel.transactionList)
    }

    private fun FragmentDashboardBinding.updateBalanceData(
        income: Double,
        expenses: Double
    ) {
        textIncome.text = income.roundToDecimals().toString()
        textExpense.text = expenses.roundToDecimals().toString()
        textBalance.text = (income - expenses).roundToDecimals().toString()
    }

    // TODO: Remove !!
    private fun FiltersUiModel.toFiltersString(): String {
        var content = getString(R.string.showing)
        content += "${showTransactions.toArgumentTypeString()} "
        val segmentPeriod =
            if (periodMode == PeriodMode.WHOLE_MONTH && yearMonth != null) getString(
                R.string.month_template,
                yearMonth.month,
                yearMonth.year
            )
            else getString(
                R.string.range_template,
                dateFrom,
                dateTo
            )
        content += "$segmentPeriod "
        val argSort = sortBy.sortName
        val argOrder = isDescending.toArgumentOrderString()
        val segmentSort = getString(R.string.sorted_by_template, argSort, argOrder)
        content += segmentSort
        return content
    }

    private fun configureNavObserver() {
        findNavController().getBackStackEntry(R.id.dashboardFragment).apply {
            val observer = createFilterObserver()
            lifecycle.addObserver(observer)
            setRemovalObserver(observer)
        }
    }

    private fun NavBackStackEntry.createFilterObserver(): LifecycleEventObserver =
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME &&
                savedStateHandle.contains(KEY_FILTERS)
            ) savedStateHandle.get<FiltersUiModel>(KEY_FILTERS)?.let {
                viewModel.updateFilters(it)
            }
        }

    private fun NavBackStackEntry.setRemovalObserver(observer: LifecycleEventObserver) {
        viewLifecycleOwner.lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    lifecycle.removeObserver(observer)
                }
            }
        )
    }

    private fun ShowTransactions?.toArgumentTypeString() = when (this) {
        ShowTransactions.SHOW_EXPENSES -> getString(R.string.all_expenses)
        ShowTransactions.SHOW_INCOMES -> getString(R.string.all_incomes)
        else -> getString(R.string.all_transactions)
    }

    private fun Boolean.toArgumentOrderString() =
        if (this) getString(R.string.descending)
        else getString(R.string.ascending)
}
