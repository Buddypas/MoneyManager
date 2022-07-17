package com.inFlow.moneyManager.presentation.filters

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.DialogFiltersBinding
import com.inFlow.moneyManager.shared.kotlin.*
import com.inFlow.moneyManager.presentation.dashboard.FieldError
import com.inFlow.moneyManager.presentation.dashboard.PeriodMode
import com.inFlow.moneyManager.presentation.dashboard.ShowTransactions
import com.inFlow.moneyManager.presentation.dashboard.SortBy
import dagger.hilt.android.AndroidEntryPoint
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class FiltersDialog : DialogFragment() {
    private var _binding: DialogFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FiltersViewModel by viewModels()
    private val args: FiltersDialogArgs by navArgs()

    private lateinit var sortAdapter: ArrayAdapter<String>
    private lateinit var monthAdapter: ArrayAdapter<String>
    private lateinit var yearAdapter: ArrayAdapter<String>

    override fun onResume() {
        setFullWidth()
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        _binding = DialogFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setInitialFilters(args.filters)
        setUpUI()
    }

    private fun setUpUI() {
        binding.root.setAsRootView()
        sortAdapter =
            ArrayAdapter(requireContext(), R.layout.item_month_dropdown, viewModel.sortOptions)
        binding.sortDropdown.setAdapter(sortAdapter)

        monthAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, MONTHS)
        binding.monthDropdown.setAdapter(monthAdapter)

        yearAdapter =
            ArrayAdapter(requireContext(), R.layout.item_month_dropdown, viewModel.years)
        binding.yearDropdown.setAdapter(yearAdapter)

        binding.yearDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                val year = binding.yearDropdown.text.toString().toInt()
                viewModel.onYearSelected(year)
            }

        binding.monthDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.onMonthSelected(position)
            }

        binding.sortDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.filters.sortBy = when (position) {
                    0 -> SortBy.SORT_BY_DATE
                    1 -> SortBy.SORT_BY_CATEGORY
                    else -> SortBy.SORT_BY_AMOUNT
                }
            }

        binding.applyBtn.setOnClickListener {
            viewModel.onApplyClicked()
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.clearBtn.setOnClickListener {
            viewModel.onClearClicked()
        }

        binding.incomesCbx.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.onTypeChecked(buttonView, isChecked)
        }

        binding.expensesCbx.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.onTypeChecked(buttonView, isChecked)
        }

        binding.periodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//            viewModel.onPeriodSelected(checkedId)
            viewModel.filters.period =
                if (checkedId == R.id.custom_range_btn) PeriodMode.CUSTOM_RANGE
                else PeriodMode.WHOLE_MONTH
            managePeriodFields(viewModel.filters.period)
        }

        binding.orderToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            viewModel.onSortOrderChecked(checkedId, isChecked)
        }

        /**
         * This method is called to notify you that, within 'text', the 'count' characters beginning at 'start'
         * have just replaced old text that had length 'before'.
         */
        binding.fromInput.doOnTextChanged { text, _, before, count ->
//            if before > count -> char deleted
            text?.let {
                var newText = it.toString()
                if (before < count) {
                    if (newText.length == 2 || newText.length == 5) {
                        newText += '/'
                        binding.fromInput.text = SpannableStringBuilder(newText)
                        binding.fromInput.setSelection(binding.fromInput.text!!.length)
                    }
                }
            }
            viewModel.fromDateString.value = binding.fromInput.text.toString()
            binding.fromLayout.error = null
        }
        binding.toInput.doOnTextChanged { text, start, before, count ->
            text?.let {
                var newText = it.toString()
                if (before < count) {
                    if (newText.length == 2 || newText.length == 5) {
                        newText += '/'
                        binding.toInput.text = SpannableStringBuilder(newText)
                        binding.toInput.setSelection(binding.toInput.text!!.length)
                    }
                }
            }
            viewModel.toDateString.value = binding.toInput.text.toString()
            binding.fromLayout.error = null
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.filtersEvent.collect {
                when (it) {
                    is FiltersEvent.ApplyFilters -> {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            KEY_FILTERS, it.filtersData
                        )
                        dismiss()
                    }
                    is FiltersEvent.ShowFieldError -> displayError(it.fieldError)
                    FiltersEvent.ClearFilters -> populateFilters()
                }
            }
        }

        populateFilters()
        managePeriodFields(viewModel.filters.period)
    }

    private fun displayError(fieldError: FieldError) {
        when (fieldError.field) {
            FieldType.FIELD_DATE_FROM -> binding.fromLayout.error =
                fieldError.message
            FieldType.FIELD_DATE_TO -> binding.toLayout.error = fieldError.message
            FieldType.FIELD_OTHER -> binding.root.showError(fieldError.message)
        }
    }

    private fun populateFilters() = binding.apply {
        val monthPosition =
            if (viewModel.filters.yearMonth != null) viewModel.filters.yearMonth!!.monthValue - 1
            else LocalDate.now().monthValue - 1
        monthDropdown.setText(
            monthAdapter.getItem(monthPosition),
            false
        )

        val year =
            if (viewModel.filters.yearMonth != null) viewModel.filters.yearMonth!!.year.toString()
            else LocalDate.now().year.toString()
        yearDropdown.setText(year, false)

        if (viewModel.filters.period == PeriodMode.WHOLE_MONTH) {
            if (periodRadioGroup.checkedRadioButtonId != R.id.whole_month_btn)
                periodRadioGroup.check(R.id.whole_month_btn)
            if(fromInput.text.toString().isNotEmpty()) fromInput.text = SpannableStringBuilder("")
            if(toInput.text.toString().isNotEmpty()) toInput.text = SpannableStringBuilder("")
        } else {
            if (periodRadioGroup.checkedRadioButtonId != R.id.custom_range_btn)
                periodRadioGroup.check(R.id.custom_range_btn)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val fromString = try {
                viewModel.filters.customRange.first?.format(formatter).orEmpty()
            } catch (e: DateTimeException) {
                ""
            }
            val toString = try {
                viewModel.filters.customRange.second?.format(formatter).orEmpty()
            } catch (e: DateTimeException) {
                ""
            }
            viewModel.fromDateString.value = fromString
            viewModel.toDateString.value = toString
            fromInput.text = SpannableStringBuilder(fromString)
            toInput.text = SpannableStringBuilder(toString)
        }
        when (viewModel.filters.show) {
            ShowTransactions.SHOW_BOTH -> {
                incomesCbx.isChecked = true
                expensesCbx.isChecked = true
            }
            ShowTransactions.SHOW_EXPENSES -> expensesCbx.isChecked = true
            ShowTransactions.SHOW_INCOMES -> incomesCbx.isChecked = true
            null -> Unit
        }

        when (viewModel.filters.sortBy) {
            SortBy.SORT_BY_DATE -> sortDropdown.setText(sortAdapter.getItem(0), false)
            SortBy.SORT_BY_CATEGORY -> sortDropdown.setText(sortAdapter.getItem(1), false)
            else -> sortDropdown.setText(sortAdapter.getItem(2), false)
        }
        if (viewModel.filters.isDescending) orderToggleGroup.check(R.id.desc_btn)
        else orderToggleGroup.check(R.id.asc_btn)
    }

    private fun managePeriodFields(period: PeriodMode) =
        binding.apply {
            if (period == PeriodMode.WHOLE_MONTH) {
                fromLbl.setTextColor(requireContext().getContextColor(R.color.gray))
                toLbl.setTextColor(requireContext().getContextColor(R.color.gray))
                fromLayout.isEnabled = false
                toLayout.isEnabled = false
                monthLbl.setTextColor(requireContext().getContextColor(R.color.black))
                monthDropdownLayout.isEnabled = true
                yearDropdownLayout.isEnabled = true
            } else {
                fromLbl.setTextColor(requireContext().getContextColor(R.color.black))
                toLbl.setTextColor(requireContext().getContextColor(R.color.black))
                fromLayout.isEnabled = true
                toLayout.isEnabled = true
                monthLbl.setTextColor(requireContext().getContextColor(R.color.gray))
                monthDropdownLayout.isEnabled = false
                yearDropdownLayout.isEnabled = false
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}