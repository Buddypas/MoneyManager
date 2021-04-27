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
import com.inFlow.moneyManager.shared.kotlin.*
import com.inFlow.moneyManager.ui.MainActivity
import kotlinx.coroutines.flow.collect
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class AddTransactionFragment : Fragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModel()

    private val incomeList = mutableListOf<Category>()
    private val expenseList = mutableListOf<Category>()

    var loadingDialog: AlertDialog? = null

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
        loadingDialog = requireContext().getLoadingDialog()

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
            incomes.observe(viewLifecycleOwner, { incomeList.addAll(it) })
            expenses.observe(viewLifecycleOwner, { expenseList.addAll(it) })
            categoriesReady.observe(viewLifecycleOwner, {
                if (it) initDropdownAdapter()
            })
            initData()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect {
                when (it) {
                    is AddTransactionEvent.ShowErrorMessage -> {
                        (requireActivity() as MainActivity).showError(it.msg)
                        Timber.e("snackbar shown")
                    }
                    is AddTransactionEvent.ShowLoading -> {
                        if (it.shouldShow) loadingDialog?.show()
                        else loadingDialog?.hide()
                    }
                    is AddTransactionEvent.ShowSuccessMessage ->
                        binding.root.showSuccessMessage(it.msg)
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
        val list = expenseList.map { it.categoryName }
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_month_dropdown,
            R.id.dropdown_txt,
            list
        )
        binding.categoryDropdown.setAdapter(categoryAdapter)
    }

    private fun onSaveClicked() {
        val desc = binding.descriptionInput.text.toString()
        val amountString = binding.amountInput.text.toString()
        if (amountString.isEmpty()) binding.amountLayout.error = "Amount must be larger than 0."
        else viewModel.saveTransaction(desc, amountString.toDouble())
    }
}