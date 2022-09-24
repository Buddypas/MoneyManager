package com.inFlow.moneyManager.presentation.addCategory

import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.domain.category.usecase.SaveCategoryUseCase
import com.inFlow.moneyManager.domain.category.usecase.UpdateCategoryUseCase
import com.inFlow.moneyManager.presentation.addCategory.extension.updateWith
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiEvent
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiModel
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiState
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import com.inFlow.moneyManager.shared.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase
) : BaseViewModel<AddCategoryUiModel, AddCategoryUiState, AddCategoryUiEvent>() {

    override fun fetchInitialState(): AddCategoryUiState = AddCategoryUiState.Idle()

    fun init(category: Category?) {
        category?.let { existingCategory ->
            updateCurrentUiStateWith {
                AddCategoryUiState.Idle(
                    it.copy(
                        categoryId = existingCategory.id,
                        categoryType = existingCategory.type,
                        categoryName = existingCategory.name
                    )
                )
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
        runCatching {
            isCategoryValid(name)
        }.mapCatching {
            it?.let { errorResId ->
                updateCurrentUiStateWith {
                    AddCategoryUiState.Error(it.copy(errorMessageResId = errorResId))
                }
                return
            }
        }.mapCatching {
            updateCurrentUiStateWith {
                requireUiState().updateWith(it.copy(categoryName = requireNotNull(name) { "Category name cannot be null" }))
            }
        }.onSuccess {
            if (requireUiState().uiModel.isUpdate())
                updateCategory()
            else saveCategory()
        }.onFailure {
            Timber.e("Failed to save form: $it")
            AddCategoryUiEvent.ShowErrorMessage(R.string.error_adding_category).emit()
        }
    }

    private fun isCategoryValid(name: String?) =
        if (name.isNullOrBlank())
            R.string.error_invalid_category_name
        else null

    private fun saveCategory() {
        viewModelScope.launch {
            runCatching {
                requireUiState().uiModel.toCategory()
            }.mapCatching { category ->
                saveCategoryUseCase.execute(category)
            }.onSuccess {
                AddCategoryUiEvent.ShowMessage(R.string.category_added).emit()
                AddCategoryUiEvent.NavigateUp.emit()
            }.onFailure {
                Timber.e("Failed to add category: $it")
                AddCategoryUiEvent.ShowErrorMessage(R.string.error_adding_category).emit()
            }
        }
    }

    private fun updateCategory() {
        viewModelScope.launch {
            runCatching {
                requireUiState().uiModel.toCategory()
            }.mapCatching { category ->
                updateCategoryUseCase.execute(category)
            }.onSuccess {
                AddCategoryUiEvent.ShowMessage(R.string.category_updated).emit()
                AddCategoryUiEvent.NavigateUp.emit()
            }.onFailure {
                Timber.e("Failed to update category: $it")
                AddCategoryUiEvent.ShowErrorMessage(R.string.error_updating_category).emit()
            }
        }
    }

    private fun AddCategoryUiModel.toCategory(): Category =
        Category(categoryId, categoryName, categoryType)
}
