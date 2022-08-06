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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.DialogFiltersBinding
import com.inFlow.moneyManager.presentation.dashboard.model.FieldError
import com.inFlow.moneyManager.presentation.dashboard.model.PeriodMode
import com.inFlow.moneyManager.presentation.dashboard.model.ShowTransactions
import com.inFlow.moneyManager.presentation.dashboard.model.SortBy
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiEvent
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiState
import com.inFlow.moneyManager.presentation.shared.extension.clear
import com.inFlow.moneyManager.presentation.shared.extension.setSpannable
import com.inFlow.moneyManager.shared.kotlin.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class FiltersDialog : DialogFragment() {
    private var _binding: DialogFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FiltersViewModel by viewModels()
    private val args: FiltersDialogArgs by navArgs()

    private var sortAdapter: ArrayAdapter<String>? = null
    private var monthAdapter: ArrayAdapter<String>? = null
    private var yearAdapter: ArrayAdapter<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DialogFiltersBinding
        .inflate(inflater, container, false)
        .also {
            isCancelable = false
            _binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setInitialFilters(args.filters)
        binding.setUpUi()
        handleState()
        handleEvents()
    }

    override fun onResume() {
        setFullWidth()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectState(this) { state ->
                    when (state) {
                        is FiltersUiState.Idle -> binding.bindIdle(state)
                        is FiltersUiState.Error -> binding.bindError(state)
                    }
                }
            }
        }
    }

    private fun handleEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectEvents(this) { event ->
                    when (event) {
                        is FiltersUiEvent.ApplyFilters -> {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                KEY_FILTERS,
                                event.filtersData
                            )
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun DialogFiltersBinding.bindError(state: FiltersUiState.Error) {
        state.uiModel.fieldError?.let { displayError(it) }
    }

    private fun DialogFiltersBinding.bindIdle(state: FiltersUiState.Idle) {
        managePeriodFields(state.uiModel.periodMode)
        val monthPosition = state.uiModel.yearMonth?.monthValue ?: (LocalDate.now().monthValue)
        dropdownMonth.setText(
            monthAdapter?.getItem(monthPosition - 1),
            false
        )

        val year = state.uiModel.yearMonth?.year ?: (LocalDate.now().year)
        dropdownYear.setText(year.toString(), false)

        if (state.uiModel.periodMode == PeriodMode.WHOLE_MONTH) {
            radioGroupPeriod.check(buttonWholeMonth.id)
            editTextFrom.clear()
            editTextTo.clear()
        } else {
            radioGroupPeriod.check(buttonCustomRange.id)
            val fromString = state.uiModel.dateFrom?.toFormattedDate().orEmpty()
            val toString = state.uiModel.dateTo?.toFormattedDate().orEmpty()
            editTextFrom.setSpannable(fromString)
            editTextTo.setSpannable(toString)
        }
        when (state.uiModel.showTransactions) {
            ShowTransactions.SHOW_BOTH -> {
                cbxIncomes.isChecked = true
                cbxExpenses.isChecked = true
            }
            ShowTransactions.SHOW_EXPENSES -> {
                cbxIncomes.isChecked = false
                cbxExpenses.isChecked = true
            }
            ShowTransactions.SHOW_INCOMES -> {
                cbxIncomes.isChecked = true
                cbxExpenses.isChecked = false
            }
            ShowTransactions.SHOW_NONE -> {
                cbxIncomes.isChecked = false
                cbxExpenses.isChecked = false
            }
        }
        when (state.uiModel.sortBy) {
            SortBy.SORT_BY_DATE -> dropdownSort.setText(sortAdapter?.getItem(0), false)
            SortBy.SORT_BY_CATEGORY -> dropdownSort.setText(sortAdapter?.getItem(1), false)
            else -> dropdownSort.setText(sortAdapter?.getItem(2), false)
        }
        if (state.uiModel.isDescending) toggleGroupOrder.check(R.id.buttonDescending)
        else toggleGroupOrder.check(R.id.buttonAscending)
    }

    private fun DialogFiltersBinding.setUpUi() {
        // TODO: Remove extension
        root.setAsRootView()
        sortAdapter =
            ArrayAdapter(requireContext(), R.layout.item_month_dropdown, viewModel.sortOptions)
        monthAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, MONTHS)
        yearAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, viewModel.years)
        dropdownSort.setAdapter(sortAdapter)
        dropdownMonth.setAdapter(monthAdapter)
        dropdownYear.setAdapter(yearAdapter)

        dropdownYear.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            val year = dropdownYear.text.toString().toInt()
            viewModel.onYearSelected(year)
        }

        dropdownMonth.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            viewModel.onMonthSelected(position)
        }

        dropdownSort.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                // TODO: Create extension which does this by string value
                viewModel.onSortByChanged(
                    when (position) {
                        0 -> SortBy.SORT_BY_DATE
                        1 -> SortBy.SORT_BY_CATEGORY
                        else -> SortBy.SORT_BY_AMOUNT
                    }
                )
            }

        buttonApply.setOnClickListener { viewModel.onApplyClicked() }
        buttonCancel.setOnClickListener { dismiss() }
        buttonClear.setOnClickListener { viewModel.onClearClicked() }

        cbxIncomes.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.onIncomesChecked()
            else viewModel.onIncomesUnchecked()
        }

        cbxExpenses.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.onExpensesChecked()
            else viewModel.onExpensesUnchecked()
        }

        radioGroupPeriod.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onPeriodModeChanged(
                if (checkedId == buttonCustomRange.id) PeriodMode.CUSTOM_RANGE
                else PeriodMode.WHOLE_MONTH
            )
        }

        toggleGroupOrder.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == buttonDescending.id) viewModel.onDescendingSelected()
                else viewModel.onAscendingSelected()
            }
        }

        editTextFrom.doOnTextChanged { text, _, before, count ->
            //  if before > count -> char deleted
            text?.let {
                var newText = it.toString()
                if (before < count) {
                    if (newText.length == 2 || newText.length == 5) {
                        newText += '/'
//                        viewModel.onFromDateChanged(newText)
                        editTextFrom.text = SpannableStringBuilder(newText)
                        editTextFrom.setSelection(editTextFrom.text!!.length)
                    }
                }
            }
//            viewModel.fromDateString.value = editTextFrom.text.toString()
            editTextLayoutFrom.error = null
        }
        editTextTo.doOnTextChanged { text, start, before, count ->
            text?.let {
                var newText = it.toString()
                if (before < count) {
                    if (newText.length == 2 || newText.length == 5) {
                        newText += '/'
                        editTextTo.text = SpannableStringBuilder(newText)
                        editTextTo.setSelection(editTextTo.text!!.length)
                    }
                }
            }
//            viewModel.toDateString.value = editTextTo.text.toString()
            editTextLayoutFrom.error = null
        }
    }

    private fun displayError(fieldError: FieldError) {
        when (fieldError.field) {
            FieldType.FIELD_DATE_FROM ->
                binding.editTextLayoutFrom.error = fieldError.message
            FieldType.FIELD_DATE_TO -> binding.editTextLayoutTo.error = fieldError.message
            FieldType.FIELD_OTHER -> binding.root.showSnackbar(fieldError.message)
        }
    }

    private fun DialogFiltersBinding.selectWholeMonth() {
        textFromLabel.setTextColor(requireContext().getContextColor(R.color.gray))
        textToLabel.setTextColor(requireContext().getContextColor(R.color.gray))
        editTextLayoutFrom.isEnabled = false
        editTextLayoutTo.isEnabled = false
        textMonthLabel.setTextColor(requireContext().getContextColor(R.color.black))
        dropdownLayoutMonth.isEnabled = true
        dropdownLayoutYear.isEnabled = true
    }

    private fun DialogFiltersBinding.selectCustomRange() {
        textFromLabel.setTextColor(requireContext().getContextColor(R.color.black))
        textToLabel.setTextColor(requireContext().getContextColor(R.color.black))
        editTextLayoutFrom.isEnabled = true
        editTextLayoutTo.isEnabled = true
        textMonthLabel.setTextColor(requireContext().getContextColor(R.color.gray))
        dropdownLayoutMonth.isEnabled = false
        dropdownLayoutYear.isEnabled = false
    }

    private fun DialogFiltersBinding.managePeriodFields(period: PeriodMode) =
        if (period == PeriodMode.WHOLE_MONTH) {
            selectWholeMonth()
        } else selectCustomRange()
}
