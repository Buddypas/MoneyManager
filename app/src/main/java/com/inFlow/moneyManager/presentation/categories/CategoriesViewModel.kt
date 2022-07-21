package com.inFlow.moneyManager.presentation.categories

import androidx.lifecycle.*
import com.inFlow.moneyManager.data.repository.CategoryRepository
import com.inFlow.moneyManager.presentation.categories.model.CategoriesUiModel
import com.inFlow.moneyManager.presentation.categories.model.CategoriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val repository: CategoryRepository) :
    ViewModel() {
    private val _stateFlow = MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading())
    private val stateFlow = _stateFlow.asStateFlow()

//    private val eventChannel = Channel<CategoriesUiEvent>()
//    private val eventFlow = eventChannel.receiveAsFlow()

    init {
        fetchCategories()
    }

    fun collectState(
        viewLifecycleOwner: LifecycleOwner,
        callback: (CategoriesUiState) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stateFlow.collectLatest { callback.invoke(it) }
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collectLatest { categoryList ->
                updateCurrentUiStateWith {
                    CategoriesUiState.Idle(CategoriesUiModel(categoryList))
                }
            }
        }
    }

    private fun updateCurrentUiStateWith(uiStateProvider: (CategoriesUiModel) -> CategoriesUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun requireUiState(): CategoriesUiState = stateFlow.value
}
