package com.inFlow.moneyManager.ui.add_transaction

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentAddTransactionBinding
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.shared.kotlin.onSelectedItemChanged
import com.inFlow.moneyManager.shared.kotlin.setValueIfDifferent
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * 1. Load expenses
 * 2. Load incomes
 * 3. Set initial category to be expense
 */
class AddTransactionFragment : Fragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTransactionViewModel by viewModel()

    private var categoryType = CategoryType.EXPENSE
    private var selectedCategoryPosition = -1

    private var categoryAdapter: ArrayAdapter<String>? = null

    val incomeList = mutableListOf<Category>()
    val expenseList = mutableListOf<Category>()

//    val activeCategoryList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.expenseBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            selectedCategoryPosition = -1
            binding.categoryDropdown.text = SpannableStringBuilder("")
            onTypeSelected(isChecked)
        }

//        binding.typeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//            selectedCategoryPosition = -1
//            binding.categoryDropdown.text = SpannableStringBuilder("")
//            onTypeSelected(checkedId)
//        }

        binding.categoryDropdown.onSelectedItemChanged {
            selectedCategoryPosition = it
        }

        with(viewModel) {
            incomes.observe(viewLifecycleOwner, { incomeList.addAll(it) })
            expenses.observe(viewLifecycleOwner, { expenseList.addAll(it) })
            categoriesReady.observe(viewLifecycleOwner, {
                if (it) initDropdownAdapter()
            })
            initData()
        }
    }

    private fun onTypeSelected(isExpenseChecked: Boolean) {
        categoryType =
            if (isExpenseChecked) CategoryType.EXPENSE
            else CategoryType.INCOME
        val list =
            if (categoryType == CategoryType.EXPENSE)
                expenseList.map { it.categoryName }
            else
                incomeList.map { it.categoryName }
        categoryAdapter?.apply {
            clear()
            addAll(list)
//            notifyDataSetChanged()
        }
    }

    private fun initDropdownAdapter() {
        if (categoryAdapter == null) {
            val list = expenseList.map { it.categoryName }
            categoryAdapter = ArrayAdapter(
                requireContext(),
                R.layout.item_month_dropdown,
                R.id.dropdown_txt,
                list
            ).apply { setNotifyOnChange(true) }
            binding.categoryDropdown.setAdapter(categoryAdapter)
        }
    }
}