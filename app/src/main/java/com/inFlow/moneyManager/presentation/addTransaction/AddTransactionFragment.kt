package com.inFlow.moneyManager.presentation.addTransaction

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentAddTransactionBinding
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiEvent
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiState
import com.inFlow.moneyManager.shared.base.BaseFragment
import com.inFlow.moneyManager.shared.kotlin.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTransactionFragment : BaseFragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentAddTransactionBinding.inflate(inflater, container, false).also {
            _binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setUpUi()

        viewModel.collectState(viewLifecycleOwner) { state ->
            when (state) {
                is AddTransactionUiState.Idle -> state.bindIdle()
                is AddTransactionUiState.Error -> state.bindError()
                is AddTransactionUiState.LoadingCategories -> Unit
            }
        }

        viewModel.collectEvents(viewLifecycleOwner) { event ->
            when (event) {
                is AddTransactionUiEvent.ShowErrorMessage -> binding.root.showError(getString(event.msgResId))
                is AddTransactionUiEvent.ShowSuccessMessage ->
                    binding.root.showSuccessMessage(event.msg)
                AddTransactionUiEvent.NavigateUp -> findNavController().navigateUp()
            }
        }
    }

    private fun FragmentAddTransactionBinding.setUpUi() {
        expenseBtn.setOnCheckedChangeListener { _, isExpensesChecked ->
            viewModel.onTypeClick(isExpensesChecked)
        }
        categoryDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.onCategoryClick(position)
            }
        cancelBtn.setOnClickListener { findNavController().navigateUp() }
        saveBtn.setOnClickListener { onSaveClick() }
    }

    private fun AddTransactionUiState.Error.bindError() {
        with(binding) {
            uiModel.categoryErrorResId?.let {
                categoryLayout.error = getString(it)
            }
            uiModel.descriptionErrorResId?.let {
                descriptionLayout.error = getString(it)
            }
            uiModel.amountErrorResId?.let {
                amountLayout.error = getString(it)
            }
        }
    }

    private fun AddTransactionUiState.Idle.bindIdle() {
        with(binding) {
            uiModel.selectedCategory?.let {
                categoryDropdown.text = SpannableStringBuilder(it.categoryName)
            }
            uiModel.activeCategoryList?.let { activeList ->
                val categoryAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.item_month_dropdown,
                    R.id.dropdown_txt,
                    activeList.map { it.categoryName }
                )
                categoryDropdown.setAdapter(categoryAdapter)
            }
        }
    }

    private fun onSaveClick() {
        val desc = binding.descriptionInput.text?.toString()
        val amountString = binding.amountInput.text?.toString()?.toDoubleOrNull()
        viewModel.onSaveClick(desc, amountString)
    }
}
