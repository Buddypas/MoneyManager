package com.inFlow.moneyManager.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding
import com.inFlow.moneyManager.presentation.dashboard.adapter.TransactionsAdapter
import com.inFlow.moneyManager.presentation.dashboard.model.*
import com.inFlow.moneyManager.shared.base.BaseFragment
import com.inFlow.moneyManager.shared.kotlin.KEY_FILTERS
import com.inFlow.moneyManager.shared.kotlin.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.LocalDate

// TODO: Think about using a subgraph for dashboard and filters and share a view model
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class DashboardFragment : BaseFragment() {
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
                            navigateSafely(
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
                        is DashboardUiState.Idle -> state.bindIdle()
                        is DashboardUiState.Loading -> Unit
                    }
                }
            }
        }
    }

    // TODO: Extract
    private fun SearchView.setQueryChangedListener(callback: (String) -> Unit) {
        onQueryTextChanged { callback.invoke(it) }
    }

    private fun FragmentDashboardBinding.setUpUi() {
        (toolbar.menu.findItem(R.id.action_search).actionView as? SearchView)?.setQueryChangedListener {
            viewModel.updateQuery(it)
        }

        textMonth.text = LocalDate.now().month.name

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_filter -> {
                    viewModel.openFilters()
                    true
                }
                else -> false
            }
        }
        transactionsAdapter = TransactionsAdapter()
        recyclerTransactions.adapter = transactionsAdapter
        buttonAdd.setOnClickListener { viewModel.onAddClicked() }
    }

    // TODO: Refactor this to a binding extension, not state
    private fun DashboardUiState.Idle.bindIdle() {
        binding.textFilters.text = uiModel.filters.toFiltersString()
        binding.updateBalanceData(uiModel.income, uiModel.expenses)
        binding.textNoTransactions.isVisible = uiModel.transactionList.isNullOrEmpty()
        transactionsAdapter?.submitList(uiModel.transactionList)
    }

    private fun FragmentDashboardBinding.updateBalanceData(
        income: Double,
        expenses: Double
    ) {
        textIncome.text = income.toString()
        textExpense.text = expenses.toString()
        textBalance.text = (income - expenses).toString()
    }

    // TODO: Remove !!
    private fun Filters.toFiltersString(): String {
        var content = getString(R.string.showing)
        content += "${show.toArgumentTypeString()} "
        val segmentPeriod =
            if (period == PeriodMode.WHOLE_MONTH && yearMonth != null) getString(
                R.string.month_template,
                yearMonth!!.month,
                yearMonth!!.year
            )
            else getString(
                R.string.range_template,
                customRange.first,
                customRange.second
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
            ) savedStateHandle.get<Filters>(KEY_FILTERS)?.let {
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
        if (this) getString(R.string.descending) else getString(R.string.ascending)
}
