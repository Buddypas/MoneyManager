package com.inFlow.moneyManager.ui.dashboard

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding
import com.inFlow.moneyManager.shared.kotlin.onQueryTextChanged
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.searchQuery.observe(viewLifecycleOwner, {
            Timber.e(it)
        })
        val wallets = listOf("Material", "Design", "Components", "Android")
        val walletAdapter = ArrayAdapter(requireContext(), R.layout.item_wallet_dropdown, wallets)
        binding.walletDropdown.setAdapter(walletAdapter)
        binding.walletDropdown.setText(walletAdapter.getItem(0).toString(), false)

        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            Timber.e("text changed: $it")
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.action_filter -> {
                    findNavController().navigate(DashboardFragmentDirections.actionDashboardToFilters())
                    true
                }
                else -> false
            }
        }
//        setHasOptionsMenu(true)

//        val months = listOf(
//            "Jan",
//            "Feb",
//            "Mar",
//            "Apr",
//            "May",
//            "Jun",
//            "Jul",
//            "Aug",
//            "Sep",
//            "Oct",
//            "Nov",
//            "Dec"
//        )
//        val monthAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, months)
//        binding.monthDropdown.setAdapter(monthAdapter)
//        binding.monthDropdown.setText(monthAdapter.getItem(0).toString(), false)
//

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}