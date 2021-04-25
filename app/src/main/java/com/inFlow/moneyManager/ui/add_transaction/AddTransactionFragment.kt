package com.inFlow.moneyManager.ui.add_transaction

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentAddTransactionBinding
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.shared.kotlin.onSelectedItemChanged
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

    private var categoryAdapter: ArrayAdapter<String>? = null

    val incomeList = mutableListOf<Category>()
    val expenseList = mutableListOf<Category>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDropdownAdapter()

        binding.typeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.updateCategoryPosition(null)
            binding.categoryDropdown.text = SpannableStringBuilder("")
            viewModel.onTypeSelected(checkedId)
        }

        binding.categoryDropdown.onSelectedItemChanged {
            viewModel.updateCategoryPosition(it)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.categoryType.collectLatest { t ->
                t?.let { type ->
                    categoryAdapter?.clear()
                    if (type == CategoryType.EXPENSE) categoryAdapter?.addAll(expenseList.map { it.categoryName })
                    else categoryAdapter?.addAll(incomeList.map { it.categoryName })
                    categoryAdapter?.notifyDataSetChanged()
                }
            }
        }

        with(viewModel) {
            incomes.observe(viewLifecycleOwner, { incomeList.addAll(it) })
            expenses.observe(viewLifecycleOwner, { expenseList.addAll(it) })
//            categoriesReady.observe(viewLifecycleOwner, {
//                if (it) initDropdownAdapter()
//            })
            initData()
        }
    }

    private fun initDropdownAdapter() {
        if (categoryAdapter == null)
            categoryAdapter = ArrayAdapter(
                requireContext(),
                R.layout.item_month_dropdown,
                expenseList.map { it.categoryName })
                .also { binding.categoryDropdown.setAdapter(it) }
    }
}