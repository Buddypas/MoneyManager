package com.inFlow.moneyManager.ui.add_transaction

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentAddTransactionBinding
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding
import com.inFlow.moneyManager.db.entities.Category
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTransactionViewModel by viewModel()
    private var categoryAdapter: ArrayAdapter<Category>? = null

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

//        categoryAdapter = ArrayAdapter<Category>(requireContext(), R.layout.item_month_dropdown)
//            .also { binding.categoryDropdown.setAdapter(it) }

        binding.typeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.onTypeSelected(checkedId)
        }

        // TODO: Continue
        with(viewModel) {
            incomes.observe(viewLifecycleOwner, {
                incomeList.addAll(it)
            })
            expenses.observe(viewLifecycleOwner, {
                expenseList.addAll(it)
                initDropdownAdapter()
            })
//            loadIncomes()
//            loadExpenses()
        }

        lifecycleScope.launch {
            viewModel.categoryType.collectLatest {
                categoryAdapter?.clear()
                if (it == CategoryType.EXPENSE) categoryAdapter?.addAll(expenseList)
                else categoryAdapter?.addAll(incomeList)
                categoryAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun initDropdownAdapter() {
        if (categoryAdapter == null) {
            categoryAdapter = ArrayAdapter<Category>(requireContext(), R.layout.item_month_dropdown, expenseList)
                    .also { binding.categoryDropdown.setAdapter(it) }
        }
    }
}