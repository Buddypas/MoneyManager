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

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddTransactionViewModel
    private lateinit var categoryAdapter: ArrayAdapter<Category>

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

        categoryAdapter = ArrayAdapter<Category>(requireContext(), R.layout.item_month_dropdown)
            .also { binding.categoryDropdown.setAdapter(it) }

        binding.typeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.onTypeSelected(checkedId)
        }

        viewModel.incomes.observe(viewLifecycleOwner, {
            incomeList.addAll(it)
        })
        viewModel.expenses.observe(viewLifecycleOwner, {
            expenseList.addAll(it)
        })

        lifecycleScope.launch {
            viewModel.categoryType.collectLatest {
                categoryAdapter.clear()
                if (it == CategoryType.EXPENSE) categoryAdapter.addAll(expenseList)
                else categoryAdapter.addAll(incomeList)
            }
        }
    }
}