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
import java.time.LocalDate

// TODO: Think about using a subgraph for dashboard and filters and share a view model
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class DashboardFragment : BaseFragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    @ExperimentalCoroutinesApi
    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var transactionsAdapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentDashboardBinding
            .inflate(inflater, container, false)
            .also { _binding = it }
            .root

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavObserver()

        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.updateQuery(it)
        }

        binding.monthTxt.text = LocalDate.now().month.name

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_filter -> {
                    viewModel.openFilters()
                    true
                }
                else -> false
            }
        }

        transactionsAdapter = TransactionsAdapter()
        binding.transactionsRecycler.adapter = transactionsAdapter

        binding.addBtn.setOnClickListener {
            viewModel.onAddClicked()
        }

        viewModel.collectEvents(viewLifecycleOwner) { event ->
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

        viewModel.collectState(viewLifecycleOwner) { state ->
            when (state) {
                is DashboardUiState.Idle -> state.bindIdle()
                is DashboardUiState.Loading -> Unit
            }
        }
    }

    // TODO: Refactor this to a binding extension, not state
    private fun DashboardUiState.Idle.bindIdle() {
        formatFilters(uiModel.filters)
        binding.updateBalanceData(uiModel.income, uiModel.expenses)
        binding.noTransactionsTxt.isVisible = uiModel.transactionList.isNullOrEmpty()
        transactionsAdapter.submitList(uiModel.transactionList)
    }

    private fun FragmentDashboardBinding.updateBalanceData(
        income: Double,
        expenses: Double
    ) {
        incomeTxt.text = income.toString()
        expenseTxt.text = expenses.toString()
        balanceTxt.text = (income - expenses).toString()
    }

    private fun formatFilters(data: Filters?) = data?.let {
        var content = "Showing "
        val argType = when (data.show) {
            ShowTransactions.SHOW_EXPENSES -> "all expenses"
            ShowTransactions.SHOW_INCOMES -> "all incomes"
            else -> "all transactions"
        }
        content += "$argType "
        val segmentPeriod =
            if (it.period == PeriodMode.WHOLE_MONTH) getString(
                R.string.month_template,
                it.yearMonth!!.month,
                it.yearMonth!!.year
            )
            else getString(
                R.string.range_template,
                it.customRange.first,
                it.customRange.second,
            )
        content += "$segmentPeriod "
        val argSort = it.sortBy.sortName
        val argOrder = if (it.isDescending) "descending" else "ascending"
        val segmentSort = getString(R.string.sorted_by_template, argSort, argOrder)
        content += segmentSort
        binding.filtersTxt.text = content
    }

    private fun setNavObserver() {
        val navController = findNavController()
        val navBackStackEntry = navController.getBackStackEntry(R.id.dashboardFragment)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME &&
                navBackStackEntry.savedStateHandle.contains(KEY_FILTERS)
            ) navBackStackEntry.savedStateHandle.get<Filters>(KEY_FILTERS)?.let {
                viewModel.updateFilters(it)
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)
        viewLifecycleOwner.lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY)
                    navBackStackEntry.lifecycle.removeObserver(observer)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
