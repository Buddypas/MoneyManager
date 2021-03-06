package com.inFlow.moneyManager.ui.filters

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.DialogFiltersBinding
import com.inFlow.moneyManager.shared.kotlin.MONTHS
import com.inFlow.moneyManager.shared.kotlin.setFullWidth
import java.time.LocalDate


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
        setUpUI()
    }

    private fun setUpUI() {
        val sortOptions = listOf(
            "Date",
            "Category",
            "Amount"
        )
        val sortAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, sortOptions)
        binding.sortDropdown.setAdapter(sortAdapter)
        binding.sortDropdown.setText(sortAdapter.getItem(0).toString(), false)

        val months = MONTHS
        val today = LocalDate.now()
        val monthPosition = today.month.ordinal

        /**
         * Contains the last 3 years starting from the current year
         */
        val years = listOf(
            today.year.toString(),
            today.minusYears(1).year.toString(),
            today.minusYears(2).year.toString()
        )

        val monthAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, months)
        binding.monthDropdown.setAdapter(monthAdapter)
        binding.monthDropdown.setText(monthAdapter.getItem(monthPosition).toString(), false)

        val yearAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, years)
        binding.yearDropdown.setAdapter(yearAdapter)
        binding.yearDropdown.setText(yearAdapter.getItem(0).toString(), false)

        binding.periodRadioGroup.check(R.id.whole_month_btn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}