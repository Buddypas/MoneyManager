package com.inFlow.moneyManager.presentation.addCategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentAddCategoryBinding
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiEvent
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiState
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import com.inFlow.moneyManager.shared.base.BaseFragment
import com.inFlow.moneyManager.shared.extension.showSnackbar
import com.inFlow.moneyManager.shared.extension.toSpannableStringBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO: Add loading states
@AndroidEntryPoint
class AddCategoryFragment : BaseFragment() {
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddCategoryViewModel by viewModels()
    private val args: AddCategoryFragmentArgs by navArgs()

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
        viewModel.init(args.category)

        handleState()
        handleEvents()
    }

    private fun handleEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectEvents(this) { event ->
                    when (event) {
                        is AddCategoryUiEvent.ShowErrorMessage -> binding.root.showSnackbar(msgResId = event.msgResId)
                        is AddCategoryUiEvent.ShowMessage ->
                            binding.root.showSnackbar(msgResId = event.msgResId)
                        AddCategoryUiEvent.NavigateUp -> findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun handleState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectState(this) { state ->
                    when (state) {
                        is AddCategoryUiState.Idle -> binding.bindIdle(state)
                        is AddCategoryUiState.Error -> binding.bindError(state)
                    }
                }
            }
        }
    }

    private fun FragmentAddCategoryBinding.setUpUi() {
        buttonCancel.setOnClickListener { findNavController().navigateUp() }
        buttonSave.setOnClickListener { onSaveClick() }
        radioExpense.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onCategoryTypeChanged(isChecked)
        }
    }

    private fun FragmentAddCategoryBinding.bindIdle(state: AddCategoryUiState.Idle) {
        with(state.uiModel) {
            radioExpense.isChecked = categoryType == CategoryType.EXPENSE
            radioIncome.isChecked = categoryType == CategoryType.INCOME
            editTextName.text = categoryName.toSpannableStringBuilder()
            if (isUpdate())
                buttonSave.text = getString(R.string.update)
        }
    }

    private fun FragmentAddCategoryBinding.bindError(state: AddCategoryUiState.Error) {
        editTextLayoutName.error = getString(state.uiModel.errorMessageResId)
    }

    private fun onSaveClick() {
        viewModel.onSaveClick(binding.editTextName.text?.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
