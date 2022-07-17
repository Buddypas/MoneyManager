package com.inFlow.moneyManager.presentation.addCategory

import androidx.lifecycle.*
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.presentation.addCategory.extension.updateWith
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiEvent
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiModel
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiState
import com.inFlow.moneyManager.presentation.addTransaction.CategoryType
import com.inFlow.moneyManager.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    private val _stateFlow: MutableStateFlow<AddCategoryUiState> =
        MutableStateFlow(AddCategoryUiState.Idle())
    private val stateFlow = _stateFlow.asStateFlow()

    private val eventChannel = Channel<AddCategoryUiEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    fun collectState(viewLifecycleOwner: LifecycleOwner, callback: (AddCategoryUiState) -> Unit) {
        viewModelScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stateFlow.collectLatest { callback.invoke(it) }
            }
        }
    }

    fun collectEvents(viewLifecycleOwner: LifecycleOwner, callback: (AddCategoryUiEvent) -> Unit) {
        viewModelScope.launch {
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

    fun onSaveClick(name: String?) {
        if (name.isNullOrBlank())
            updateCurrentUiStateWith {
                AddCategoryUiState.Error(it.copy(errorMessageResId = R.string.error_empty_category_name))
            }
        else saveCategory(name)
    }

    private fun saveCategory(name: String) {
        viewModelScope.launch {
            runCatching {
                requireUiState().uiModel
            }.map {
                it.categoryType
            }.onSuccess { categoryType ->
                withContext(Dispatchers.IO) {
                    repository.saveCategory(categoryType, name)
                }
                withContext(Dispatchers.Main) {
                    showSuccess("Category added.")
                    navigateUp()
                }
            }
        }
    }

    private fun updateCurrentUiStateWith(uiStateProvider: (AddCategoryUiModel) -> AddCategoryUiState) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    private suspend fun showSuccess(msg: String) {
        eventChannel.send(AddCategoryUiEvent.ShowSuccessMessage(msg))
    }

    private suspend fun navigateUp() {
        eventChannel.send(AddCategoryUiEvent.NavigateUp)
    }

    private fun requireUiState(): AddCategoryUiState = stateFlow.value
}
