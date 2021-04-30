package com.inFlow.moneyManager.ui.dashboard

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding
import com.inFlow.moneyManager.shared.kotlin.KEY_FILTERS
import com.inFlow.moneyManager.shared.kotlin.onQueryTextChanged
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.time.LocalDate

class DashboardFragment : Fragment() {
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

        binding.monthTxt.text = LocalDate.now().month.name

        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
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
            findNavController().navigate(DashboardFragmentDirections.actionDashboardToAddTransaction())
        }

        lifecycleScope.launch {
            viewModel.activeFilters.collectLatest {
                formatFilters(it)
            }
        }

        lifecycleScope.launch {
            viewModel.fetchBalanceData().collectLatest {
                binding.incomeTxt.text = it.first.toString()
                binding.expenseTxt.text = it.second.toString()
                binding.balanceTxt.text = (it.first - it.second).toString()
            }
        }

        viewModel.transactions.observe(viewLifecycleOwner, {
            transactionsAdapter.submitList(it)
        })
    }

    private fun formatFilters(data: FiltersDto?) = data?.let {
        val argType = when {
            it.showExpenses && it.showIncomes -> "all transactions"
            it.showExpenses -> "all expenses"
            else -> "all incomes"
        }
        val argPeriod =
            if (it.period == PeriodMode.WHOLE_MONTH) "in ${it.fromDate?.month?.name} of ${it.fromDate?.year}"
            else "from ${it.fromDate} to ${it.toDate}"
        val argSort = it.sortBy
        val argOrder = if (it.isDescending) "descending" else "ascending"
        binding.filtersTxt.text =
            getString(R.string.filters_template, argType, argPeriod, argSort, argOrder)
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
        if (findNavController().currentDestination?.id == R.id.dashboardFragment)
            findNavController().navigate(R.id.filtersDialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}