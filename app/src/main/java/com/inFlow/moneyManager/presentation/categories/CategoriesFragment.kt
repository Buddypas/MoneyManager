package com.inFlow.moneyManager.presentation.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inFlow.moneyManager.databinding.FragmentCategoriesBinding
import com.inFlow.moneyManager.presentation.categories.adapter.CategoriesAdapter
import com.inFlow.moneyManager.presentation.categories.model.CategoriesUiState
import com.inFlow.moneyManager.shared.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

// TODO: Add pagination
// TODO: Add keyboard closing to main activity
@AndroidEntryPoint
class CategoriesFragment : BaseFragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCategoriesBinding.inflate(
        inflater,
        container,
        false
    ).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setUpUi()

        viewModel.collectState(viewLifecycleOwner) { state ->
            when (state) {
                is CategoriesUiState.Idle -> state.bindIdle()
                is CategoriesUiState.Loading -> state.bindLoading()
            }
        }
    }

    private fun FragmentCategoriesBinding.setUpUi() {
        addBtn.setOnClickListener {
            findNavController().navigate(CategoriesFragmentDirections.actionCategoriesToAddCategory())
        }
    }

    private fun CategoriesUiState.Loading.bindLoading() {
        binding.progressBar.isVisible = true
    }

    private fun CategoriesUiState.Idle.bindIdle() {
        with(binding) {
            progressBar.isGone = true
            CategoriesAdapter().also {
                categoriesRecycler.adapter = it
                it.submitList(this@bindIdle.uiModel.categoryList)
            }
        }
    }
}
