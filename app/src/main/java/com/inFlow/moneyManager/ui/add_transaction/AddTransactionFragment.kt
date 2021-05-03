package com.inFlow.moneyManager.ui.add_transaction

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentAddTransactionBinding
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.shared.base.BaseFragment
import com.inFlow.moneyManager.shared.kotlin.*
import com.inFlow.moneyManager.ui.MainActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class AddTransactionFragment : BaseFragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModel()

    private val incomeList = mutableListOf<Category>()
    private val expenseList = mutableListOf<Category>()

    private var isDropdownInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addRoot.setAsRootView()

        binding.expenseBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.selectedCategoryPosition = -1
            binding.categoryDropdown.text = SpannableStringBuilder("")
            onTypeSelected(isChecked)
        }

        binding.categoryDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.selectedCategoryPosition = position
            }

        binding.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.saveBtn.setOnClickListener {
            onSaveClicked()
        }

        with(viewModel) {
//            incomes.observe(viewLifecycleOwner, { incomeList.addAll(it) })
//            expenses.observe(viewLifecycleOwner, { expenseList.addAll(it) })
//            categoriesReady.observe(viewLifecycleOwner, {
//                if (it) initDropdownAdapter()
//            })
            initData()
        }

//        lifecycleScope.launch {
//            viewModel.categoriesReady.collectLatest {
//                if (it) initDropdownAdapter()
//            }
//        }

        lifecycleScope.launch {
            viewModel.incomeFlow.collectLatest {
                it?.let { list -> incomeList.addAll(list) }
            }
        }

        lifecycleScope.launch {
            viewModel.expenseFlow.collectLatest {
                it?.let { list ->
                    expenseList.addAll(list)
                    if (!isDropdownInitialized) initDropdownAdapter()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is AddTransactionEvent.ShowErrorMessage -> binding.root.showError(event.msg)
                    is AddTransactionEvent.ShowSuccessMessage ->
                        binding.root.showSuccessMessage(event.msg)
                    AddTransactionEvent.NavigateUp -> findNavController().navigateUp()
                }
            }
        }
    }

    private fun onTypeSelected(isExpenseChecked: Boolean) {
        viewModel.categoryType =
            if (isExpenseChecked) CategoryType.EXPENSE
            else CategoryType.INCOME
        val list =
            if (viewModel.categoryType == CategoryType.EXPENSE)
                expenseList.map { it.categoryName }
            else
                incomeList.map { it.categoryName }
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_month_dropdown,
            R.id.dropdown_txt,
            list
        )
        binding.categoryDropdown.setAdapter(categoryAdapter)
    }

    private fun initDropdownAdapter() {
        val list = expenseList.map {
            it.categoryName
        }
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_month_dropdown,
            R.id.dropdown_txt,
            list
        )
        binding.categoryDropdown.setAdapter(categoryAdapter)
        isDropdownInitialized = true
    }

    private fun onSaveClicked() {
        val desc = binding.descriptionInput.text.toString()
        val amountString = binding.amountInput.text.toString()
        when {
            viewModel.selectedCategoryPosition < 0 -> binding.root.showError("You must select a category.")
            desc.isBlank() -> binding.descriptionLayout.error = "You must enter a description."
            amountString.isEmpty() -> binding.amountLayout.error = "Amount must be larger than 0."
            else -> viewModel.saveTransaction(desc, amountString.toDouble())
        }
    }
}