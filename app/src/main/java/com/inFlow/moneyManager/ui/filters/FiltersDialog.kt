package com.inFlow.moneyManager.ui.filters

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.DialogFiltersBinding
import com.inFlow.moneyManager.shared.kotlin.setFullWidth


class FiltersDialog : DialogFragment() {
    private var _binding: DialogFiltersBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        setFullWidth()
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sortOptions = listOf(
            "Date",
            "Category",
            "Amount",
        )
        val sortAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, sortOptions)
        binding.sortDropdown.setAdapter(sortAdapter)
        binding.sortDropdown.setText(sortAdapter.getItem(0).toString(), false)

        val months = listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        val monthAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, months)
        binding.monthDropdown.setAdapter(monthAdapter)
        binding.monthDropdown.setText(monthAdapter.getItem(0).toString(), false)

        val years = listOf(
            "2021",
            "2020",
            "2019"
        )
        val yearAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, years)
        binding.yearDropdown.setAdapter(yearAdapter)
        binding.yearDropdown.setText(yearAdapter.getItem(0).toString(), false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}