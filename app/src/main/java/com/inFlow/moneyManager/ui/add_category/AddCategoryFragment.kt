package com.inFlow.moneyManager.ui.add_category

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentAddCategoryBinding
import com.inFlow.moneyManager.databinding.FragmentAddTransactionBinding
import com.inFlow.moneyManager.shared.kotlin.setAsRootView
import com.inFlow.moneyManager.shared.kotlin.showError
import com.inFlow.moneyManager.shared.kotlin.showSuccessMessage
import com.inFlow.moneyManager.ui.add_transaction.AddTransactionEvent
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.viewModel

class AddCategoryFragment : Fragment() {
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddCategoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setAsRootView()

        binding.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.saveBtn.setOnClickListener {
            onSaveClicked()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is AddCategoryEvent.ShowErrorMessage -> binding.root.showError(event.msg)
                    is AddCategoryEvent.ShowSuccessMessage ->
                        binding.root.showSuccessMessage(event.msg)
                    AddCategoryEvent.NavigateUp -> findNavController().navigateUp()
                }
            }
        }
    }

    private fun onSaveClicked() {
        val name = binding.nameInput.text.toString()
        if (name.isBlank()) binding.nameLayout.error = "You must enter a category name."
        else viewModel.saveCategory(name)
    }
}