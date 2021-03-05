package com.inFlow.moneyManager.ui.dashboard

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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