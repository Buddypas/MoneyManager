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
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding
import com.inFlow.moneyManager.shared.kotlin.KEY_FILTERS
import com.inFlow.moneyManager.shared.kotlin.onQueryTextChanged
import com.inFlow.moneyManager.ui.filters.PeriodMode
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
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
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavObserver()
        val wallets = listOf("Material", "Design", "Components", "Android")
        val walletAdapter = ArrayAdapter(requireContext(), R.layout.item_wallet_dropdown, wallets)
        binding.walletDropdown.setAdapter(walletAdapter)
        binding.walletDropdown.setText(walletAdapter.getItem(0).toString(), false)

        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_filter -> {
                    // TODO: Improve
                    val navController = findNavController()
                    if(navController.currentDestination?.id == R.id.dashboardFragment) {
                        val action = DashboardFragmentDirections.actionDashboardToFilters(
                            viewModel.activeFilters.value
                        )
                        navController.navigate(action)
                        true
                    }
                    else false
                }
                else -> false
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeFilters.collectLatest {
                formatFilters(it)
            }
        }
    }

    private fun formatFilters(data: FiltersDto?) =
        data?.let {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}