package com.inFlow.moneyManager.ui.filters

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.DialogFiltersBinding
import com.inFlow.moneyManager.shared.kotlin.*
import com.inFlow.moneyManager.vo.FiltersDto
import org.koin.android.viewmodel.ext.android.viewModel
import java.time.*
import java.time.format.DateTimeFormatter

class FiltersDialog : DialogFragment() {
    private var _binding: DialogFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FiltersViewModel by viewModel()
    private val args: FiltersDialogArgs by navArgs()

    private lateinit var sortAdapter: ArrayAdapter<String>
    private lateinit var monthAdapter: ArrayAdapter<String>

    override fun onResume() {
        setFullWidth()
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.initialFilters = args.filterData
        viewModel.activeFilters = args.filterData
        _binding = DialogFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
    }

    private fun setUpUI() {
        manageFields(viewModel.initialFilters)
        val sortOptions = listOf(
            SORT_BY_DATE,
            SORT_BY_CATEGORY,
            SORT_BY_AMOUNT
        )
        sortAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, sortOptions)
        binding.sortDropdown.setAdapter(sortAdapter)
        binding.sortDropdown.setText(sortAdapter.getItem(0).toString(), false)

        val months = MONTHS
        val today = LocalDate.now()

        /**
         * Contains the last 3 years starting from the current year
         */
        val years = listOf(
            today.year.toString(),
            today.minusYears(1).year.toString(),
            today.minusYears(2).year.toString()
        )

        monthAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, months)
        binding.monthDropdown.setAdapter(monthAdapter)

        val yearAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, years)
        binding.yearDropdown.setAdapter(yearAdapter)

        binding.periodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onPeriodSelected(checkedId)
        }

        binding.orderToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            viewModel.onSortOrderChecked(checkedId,isChecked)
        }
    }

    private fun manageFields(filterData: FiltersDto) {
        binding.apply {
            if (filterData.period == PeriodMode.WHOLE_MONTH) {
                fromLbl.setTextColor(requireContext().getContextColor(R.color.gray))
                toLbl.setTextColor(requireContext().getContextColor(R.color.gray))
                fromLayout.isEnabled = false
                toLayout.isEnabled = false
                monthLbl.setTextColor(requireContext().getContextColor(R.color.black))
                monthDropdownLayout.isEnabled = true
                yearDropdownLayout.isEnabled = true

                monthDropdown.setText(monthAdapter.getItem(filterData.monthAndYear.month.ordinal), false)
                yearDropdown.setText(monthAdapter.getItem(filterData.monthAndYear.year), false)
            } else {
                fromLbl.setTextColor(requireContext().getContextColor(R.color.black))
                toLbl.setTextColor(requireContext().getContextColor(R.color.black))
                fromLayout.isEnabled = true
                toLayout.isEnabled = true
                monthLbl.setTextColor(requireContext().getContextColor(R.color.gray))
                monthDropdownLayout.isEnabled = false
                yearDropdownLayout.isEnabled = false

                val formatter = DateTimeFormatter.ofPattern("dd/mm/yyyy")
                val fromString = filterData.fromDate?.format(formatter).orEmpty()
                val toString = filterData.toDate?.format(formatter).orEmpty()

                fromInput.text = SpannableStringBuilder(fromString)
                toInput.text = SpannableStringBuilder(toString)
            }
            if(filterData.isDescending) orderToggleGroup.check(R.id.desc_btn)
            else orderToggleGroup.check(R.id.asc_btn)
            if(filterData.showIncomes) incomesCbx.isChecked = true
            if(filterData.showExpenses) expensesCbx.isChecked = true
            when(filterData.sortBy) {
                SORT_BY_DATE -> sortDropdown.setText(sortAdapter.getItem(0), false)
                SORT_BY_CATEGORY -> sortDropdown.setText(sortAdapter.getItem(1), false)
                else -> sortDropdown.setText(sortAdapter.getItem(2), false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}