package com.inFlow.moneyManager.presentation.addTransaction

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentAddTransactionBinding
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiEvent
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiState
import com.inFlow.moneyManager.shared.base.BaseFragment
import com.inFlow.moneyManager.shared.kotlin.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        handleState()
        handleEvents()
    }

    private fun handleState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectState(this) { state ->
                    when (state) {
                        is AddTransactionUiState.Idle -> binding.bindIdle(state)
                        is AddTransactionUiState.Error -> binding.bindError(state)
                        is AddTransactionUiState.LoadingCategories -> Unit
                    }
                }
            }
        }
    }

    private fun handleEvents() {
        // TODO: Test coroutine collection
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectEvents(this) { event ->
                    when (event) {
                        is AddTransactionUiEvent.ShowErrorMessage ->
                            binding.root.showSnackbar(
                                getString(event.msgResId)
                            )
                        is AddTransactionUiEvent.ShowSuccessMessage ->
                            binding.root.showSnackbar(msgResId = event.msgResId)
                        AddTransactionUiEvent.NavigateUp -> findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun FragmentAddTransactionBinding.setUpUi() {
        buttonExpense.setOnCheckedChangeListener { _, isExpensesChecked ->
            viewModel.onTypeClick(isExpensesChecked)
        }
        dropdownCategory.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.onCategoryClick(position)
            }
        editTextLayoutDate.addLiveDateFormatter()
        buttonCancel.setOnClickListener { findNavController().navigateUp() }
        buttonSave.setOnClickListener { onSaveClick() }
    }

    private fun FragmentAddTransactionBinding.bindIdle(state: AddTransactionUiState.Idle) {
        state.uiModel.selectedCategory?.let {
            dropdownCategory.text = SpannableStringBuilder(it.name)
        }
        state.uiModel.activeCategoryList?.let { activeList ->
            ArrayAdapter(
                requireContext(),
                R.layout.item_month_dropdown,
                R.id.dropdown_txt,
                activeList.map { it.name }
            ).apply { dropdownCategory.setAdapter(this) }
        }
    }

    private fun FragmentAddTransactionBinding.bindError(state: AddTransactionUiState.Error) {
        clearErrors()
        state.uiModel.fieldError?.let { fieldError ->
            val field = when (fieldError.fieldType) {
                FieldType.CATEGORY -> editTextLayoutCategory
                FieldType.DESCRIPTION -> editTextLayoutDescription
                FieldType.AMOUNT -> editTextLayoutAmount
                FieldType.DATE -> editTextLayoutDate
                else -> null
            }
            field?.error = getString(fieldError.errorResId)
        }
    }

    private fun FragmentAddTransactionBinding.clearErrors() {
        editTextLayoutCategory.error = null
        editTextLayoutDescription.error = null
        editTextLayoutAmount.error = null
        editTextLayoutDate.error = null
    }

    private fun onSaveClick() {
        val desc = binding.editTextDescription.text?.toString()
        val amount = binding.editTextAmount.text?.toString()?.toDoubleOrNull()
        val date = binding.editTextDate.text?.toString()?.toLocalDate()
        viewModel.onSaveClick(desc, amount, date)
    }
}
