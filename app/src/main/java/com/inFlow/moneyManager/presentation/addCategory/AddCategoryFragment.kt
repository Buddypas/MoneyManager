package com.inFlow.moneyManager.presentation.addCategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.databinding.FragmentAddCategoryBinding
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiEvent
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiState
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import com.inFlow.moneyManager.shared.base.BaseFragment
import com.inFlow.moneyManager.shared.kotlin.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

// TODO: Add loading states
@AndroidEntryPoint
class AddCategoryFragment : BaseFragment() {
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddCategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentAddCategoryBinding
            .inflate(inflater, container, false)
            .also { _binding = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setUpUi()

        viewModel.collectState(viewLifecycleOwner) { state ->
            when (state) {
                is AddCategoryUiState.Idle -> binding.bindIdle(state)
                is AddCategoryUiState.Error -> binding.bindError(state)
            }
        }

        viewModel.collectEvents(viewLifecycleOwner) { event ->
            when (event) {
                is AddCategoryUiEvent.ShowErrorMessage -> binding.root.showSnackbar(msgResId = event.msgResId)
                is AddCategoryUiEvent.ShowMessage ->
                    binding.root.showSnackbar(msgResId = event.msgResId)
                AddCategoryUiEvent.NavigateUp -> findNavController().navigateUp()
            }
        }
    }

    private fun FragmentAddCategoryBinding.setUpUi() {
        cancelBtn.setOnClickListener { findNavController().navigateUp() }
        saveBtn.setOnClickListener { onSaveClick() }
        expenseRadio.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onCategoryTypeChanged(isChecked)
        }
    }

    private fun FragmentAddCategoryBinding.bindIdle(state: AddCategoryUiState.Idle) {
        expenseRadio.isChecked = state.uiModel.categoryType == CategoryType.EXPENSE
        incomeRadio.isChecked = state.uiModel.categoryType == CategoryType.INCOME
    }

    private fun FragmentAddCategoryBinding.bindError(state: AddCategoryUiState.Error) {
        nameLayout.error = getString(state.uiModel.errorMessageResId)
    }

    private fun onSaveClick() {
        viewModel.onSaveClick(binding.nameInput.text?.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
