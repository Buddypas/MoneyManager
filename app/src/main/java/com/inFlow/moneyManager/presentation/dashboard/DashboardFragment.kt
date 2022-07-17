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
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding
import com.inFlow.moneyManager.shared.base.BaseFragment
import com.inFlow.moneyManager.shared.kotlin.KEY_FILTERS
import com.inFlow.moneyManager.shared.kotlin.onQueryTextChanged
import com.inFlow.moneyManager.vo.FiltersDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

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
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavObserver()
        viewModel.fetchBalanceData()

        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        binding.monthTxt.text = LocalDate.now().month.name

        searchView.onQueryTextChanged {
            viewModel.query.value = it
        }

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

        lifecycleScope.launch {
            viewModel.activeFilters.collectLatest {
                formatFilters(it)
            }
        }

        lifecycleScope.launch {
            viewModel.balanceData.collectLatest {
                it?.let { data ->
                    binding.apply {
                        incomeTxt.text = data.first.toString()
                        expenseTxt.text = data.second.toString()
                        balanceTxt.text = (data.first - data.second).toString()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactionList.collectLatest {
                binding.noTransactionsTxt.isVisible = it.isNullOrEmpty()
                transactionsAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collectLatest { event ->
                    when (event) {
                        DashboardEvent.NavigateToAddTransaction -> {
                            val action =
                                DashboardFragmentDirections.actionDashboardToAddTransaction()
                            findNavController().navigate(action)
                        }
                        is DashboardEvent.OpenFilters -> {
                            val action =
                                DashboardFragmentDirections.actionDashboardToFilters(event.filters)
                            navigateSafely(action)
                        }
                    }
                }
            }
        }
    }

    private fun formatFilters(data: FiltersDto?) = data?.let {
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
            ) navBackStackEntry.savedStateHandle.get<FiltersDto>(KEY_FILTERS)?.let {
                viewModel.activeFilters.value = it
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
