package com.inFlow.moneyManager.ui.dashboard

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding
import com.inFlow.moneyManager.shared.kotlin.getContextDrawable
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)

        val searchItem = menu.findItem(R.id.search).apply {
            setIcon(R.drawable.ic_search)
        }

        val searchView = (searchItem.actionView as SearchView)
        searchView.apply {
            imeOptions = EditorInfo.IME_ACTION_DONE
            queryHint = "Search entries"
            isSubmitButtonEnabled = true
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchQuery.value = newText
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
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
//        val sortOptions = listOf(
//            "Date",
//            "Category",
//            "Amount",
//        )
//        val sortAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, sortOptions)
//        binding.monthDropdown.setAdapter(sortAdapter)
//        binding.monthDropdown.setText(sortAdapter.getItem(0).toString(), false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}