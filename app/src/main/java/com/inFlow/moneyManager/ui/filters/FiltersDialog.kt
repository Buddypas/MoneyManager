package com.inFlow.moneyManager.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.DialogFiltersBinding

class FiltersDialog : DialogFragment() {
    private var _binding: DialogFiltersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sortOptions = listOf(
            "Date",
            "Category",
            "Amount",
        )
        val sortAdapter = ArrayAdapter(requireContext(), R.layout.item_month_dropdown, sortOptions)
        binding.sortDropdown.setAdapter(sortAdapter)
        binding.sortDropdown.setText(sortAdapter.getItem(0).toString(), false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}