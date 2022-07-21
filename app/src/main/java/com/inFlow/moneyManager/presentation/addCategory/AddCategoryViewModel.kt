package com.inFlow.moneyManager.presentation.addCategory

import androidx.lifecycle.*
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.data.repository.CategoryRepository
import com.inFlow.moneyManager.presentation.addCategory.extension.updateWith
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiEvent
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiModel
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiState
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(private val repository: CategoryRepository) :
    ViewModel() {

    private val _stateFlow: MutableStateFlow<AddCategoryUiState> =
        MutableStateFlow(AddCategoryUiState.Idle())
    private val stateFlow = _stateFlow.asStateFlow()

    private val eventChannel = Channel<AddCategoryUiEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    fun collectState(viewLifecycleOwner: LifecycleOwner, callback: (AddCategoryUiState) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stateFlow.collectLatest { callback.invoke(it) }
            }
        }
    }

    fun collectEvents(viewLifecycleOwner: LifecycleOwner, callback: (AddCategoryUiEvent) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventFlow.collectLatest { callback.invoke(it) }
            }
        }
    }

    fun onCategoryTypeChanged(isChecked: Boolean) {
        updateCurrentUiStateWith {
            requireUiState().updateWith(
                it.copy(
                    categoryType =
                    if (isChecked) CategoryType.EXPENSE
                    else CategoryType.INCOME
                )
            )
        }
    }

    // TODO: Refactor
    fun onSaveClick(name: String?) {
        isCategoryValid(name)?.let { errorResId ->
            updateCurrentUiStateWith {
                AddCategoryUiState.Error(it.copy(errorMessageResId = errorResId))
            }
        } ?: saveCategory(name!!)
    }

    private fun isCategoryValid(name: String?) =
        if (name.isNullOrBlank()) R.string.error_invalid_category_name else null

    private fun saveCategory(name: String) {
        viewModelScope.launch {
            runCatching {
                requireUiState().uiModel
            }.map {
                it.categoryType
            }.onSuccess { categoryType ->
                repository.saveCategory(categoryType, name)
                AddCategoryUiEvent.ShowSuccessMessage("CategoryDto added.").emit()
                AddCategoryUiEvent.NavigateUp.emit()
            }
        }
    }

    private fun updateCurrentUiStateWith(uiStateProvider: (AddCategoryUiModel) -> AddCategoryUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private fun AddCategoryUiEvent.emit() = viewModelScope.launch {
        eventChannel.send(this@emit)
    }

    private fun requireUiState(): AddCategoryUiState = stateFlow.value
}
