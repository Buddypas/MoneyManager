package com.inFlow.moneyManager.ui.filters

import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.DialogFiltersBinding
import com.inFlow.moneyManager.shared.kotlin.*
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.time.*
import java.time.format.DateTimeFormatter

class FiltersDialog : DialogFragment() {
    private var _binding: DialogFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FiltersViewModel by viewModel()
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
        isCancelable = true
        _binding = DialogFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setFilters(args.filterData)
        setUpUI()
    }

    private fun setUpUI() {
        sortAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, viewModel.sortOptions)
        binding.sortDropdown.setAdapter(sortAdapter)

        monthAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, MONTHS)
        binding.monthDropdown.setAdapter(monthAdapter)

        yearAdapter =
            ArrayAdapter(requireContext(), R.layout.item_month_dropdown, viewModel.years)
        binding.yearDropdown.setAdapter(yearAdapter)

        binding.yearDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.onYearSelected(position)
            }

        binding.monthDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.onMonthSelected(position)
            }

        binding.periodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onPeriodSelected(checkedId)
        }

        binding.orderToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            viewModel.onSortOrderChecked(checkedId, isChecked)
        }

        /**
         * This method is called to notify you that, within 'text', the 'count' characters beginning at 'start'
         * have just replaced old text that had length 'before'.
         */
        binding.fromInput.doOnTextChanged { text, start, before, count ->
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
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fromDateString.collectLatest { text ->
                Timber.e(text)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.filtersEvent.collect {
                when(it) {
                    is FiltersEvent.PeriodEvent -> manageFields(it.newPeriodMode)
                }
            }
        }

        populateFilters()
        manageFields(viewModel.period.value)
    }

    private fun populateFilters() {
        if (viewModel.period.value == PeriodMode.WHOLE_MONTH)
            binding.apply {
                monthDropdown.setText(
                    monthAdapter.getItem(viewModel.month),
                    false
                )
                yearDropdown.setText(viewModel.year.toString(), false)
            }
        else {
            val formatter = DateTimeFormatter.ofPattern("dd/mm/yyyy")
            val fromString = viewModel.fromDate?.format(formatter).orEmpty()
            val toString = viewModel.toDate?.format(formatter).orEmpty()

            viewModel.fromDateString.value = fromString
            viewModel.toDateString.value = toString
        }
        binding.apply {
            if (viewModel.isDescending) orderToggleGroup.check(R.id.desc_btn)
            else orderToggleGroup.check(R.id.asc_btn)
            if (viewModel.showIncomes) incomesCbx.isChecked = true
            if (viewModel.showExpenses) expensesCbx.isChecked = true
            when (viewModel.sortBy) {
                SORT_BY_DATE -> sortDropdown.setText(sortAdapter.getItem(0), false)
                SORT_BY_CATEGORY -> sortDropdown.setText(sortAdapter.getItem(1), false)
                else -> sortDropdown.setText(sortAdapter.getItem(2), false)
            }
        }
    }

    private fun manageFields(period: PeriodMode) {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}