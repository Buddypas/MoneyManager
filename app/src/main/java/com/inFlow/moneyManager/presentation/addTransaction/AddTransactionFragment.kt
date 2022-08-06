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
                        is AddTransactionUiState.Idle -> state.bindIdle()
                        is AddTransactionUiState.Error -> state.bindError()
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
                        is AddTransactionUiEvent.ShowErrorMessage -> binding.root.showSnackbar(
                            getString(
                                event.msgResId
                            )
                        )
                        is AddTransactionUiEvent.ShowSuccessMessage ->
                            binding.root.showSnackbar(msg = event.msg)
                        AddTransactionUiEvent.NavigateUp -> findNavController().navigateUp()
                    }
                }
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
                ArrayAdapter(
                    requireContext(),
                    R.layout.item_month_dropdown,
                    R.id.dropdown_txt,
                    activeList.map { it.categoryName }
                ).apply { categoryDropdown.setAdapter(this) }
            }
        }
    }

    private fun onSaveClick() {
        val desc = binding.descriptionInput.text?.toString()
        val amountString = binding.amountInput.text?.toString()?.toDoubleOrNull()
        viewModel.onSaveClick(desc, amountString)
    }
}
