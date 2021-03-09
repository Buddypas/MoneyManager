package com.inFlow.moneyManager.ui.filters

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.DialogFiltersBinding
import com.inFlow.moneyManager.shared.kotlin.MONTHS
import com.inFlow.moneyManager.shared.kotlin.setFullWidth
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class FiltersDialog : DialogFragment() {
    private var _binding: DialogFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FiltersViewModel by viewModel()

    companion object {
        const val WHOLE_MONTH = 1
        const val CUSTOM_RANGE = 2
    }

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

        binding.periodRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.whole_month_btn) {
                Timber.e("checked whole_month_btn")
                viewModel.currentPeriodMode.postValue(WHOLE_MONTH)
            } else {
                Timber.e("checked custom_range")
                viewModel.currentPeriodMode.postValue(CUSTOM_RANGE)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}