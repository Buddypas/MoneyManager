package com.inFlow.moneyManager.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.databinding.FragmentCategoriesBinding
import com.inFlow.moneyManager.shared.kotlin.setAsRootView
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoriesAdapter: CategoriesAdapter

    private val viewModel: CategoriesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setAsRootView()

        categoriesAdapter = CategoriesAdapter()
        binding.categoriesRecycler.adapter = categoriesAdapter

        binding.addBtn.setOnClickListener {
            findNavController().navigate(CategoriesFragmentDirections.actionCategoriesToAddCategory())
        }

//        lifecycleScope.launch {
        viewModel.categories.observe(viewLifecycleOwner, {
            categoriesAdapter.submitList(it)
        })
//        }
    }

}