package com.inFlow.moneyManager.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding
import com.inFlow.moneyManager.shared.base.BaseFragment
import com.inFlow.moneyManager.shared.kotlin.KEY_FILTERS
import com.inFlow.moneyManager.shared.kotlin.onQueryTextChanged
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.time.LocalDate

class DashboardFragment : BaseFragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    @ExperimentalCoroutinesApi
    private val viewModel: DashboardViewModel by sharedViewModel()

    private lateinit var transactionsAdapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
                    showFiltersDialog()
                    true
                }
                else -> false
            }
        }

        transactionsAdapter = TransactionsAdapter()
        binding.transactionsRecycler.adapter = transactionsAdapter

        binding.addBtn.setOnClickListener {
            viewModel.onAddClicked()
//            val action = DashboardFragmentDirections.actionDashboardToAddTransaction()
//            findNavController().navigate(action)
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

        lifecycleScope.launch {
            viewModel.transactionList.collectLatest {
                binding.noTransactionsTxt.isVisible = it.isNullOrEmpty()
                transactionsAdapter.submitList(it)
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
            if (event == Lifecycle.Event.ON_RESUME
                && navBackStackEntry.savedStateHandle.contains(KEY_FILTERS)
            ) navBackStackEntry.savedStateHandle.get<FiltersDto>(KEY_FILTERS)?.let {
                viewModel.activeFilters.value = it
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY)
                navBackStackEntry.lifecycle.removeObserver(observer)
        })
    }

    private fun showFiltersDialog() {
        val action =
            DashboardFragmentDirections.actionDashboardToFilters(viewModel.activeFilters.value)
        navigateSafely(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}