package com.inFlow.moneyManager.presentation.categories

import androidx.lifecycle.*
import com.inFlow.moneyManager.domain.category.usecase.GetCategoriesUseCase
import com.inFlow.moneyManager.presentation.categories.model.CategoriesUiModel
import com.inFlow.moneyManager.presentation.categories.model.CategoriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) :
    ViewModel() {
    private val _stateFlow = MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading())
    private val stateFlow = _stateFlow.asStateFlow()

//    private val eventChannel = Channel<CategoriesUiEvent>()
//    private val eventFlow = eventChannel.receiveAsFlow()

    init {
        fetchCategories()
    }

    fun collectState(
        coroutineScope: CoroutineScope,
        callback: (CategoriesUiState) -> Unit
    ) {
        coroutineScope.launch {
            stateFlow.collectLatest { callback.invoke(it) }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            runCatching {
                getCategoriesUseCase.execute()
            }.onSuccess { categoryList ->
                updateCurrentUiStateWith {
                    CategoriesUiState.Idle(CategoriesUiModel(categoryList))
                }
            }.onFailure {
                Timber.e("Failed to fetch categories: $it")
            }
        }
    }

    private fun updateCurrentUiStateWith(uiStateProvider: (CategoriesUiModel) -> CategoriesUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun requireUiState(): CategoriesUiState = stateFlow.value
}
