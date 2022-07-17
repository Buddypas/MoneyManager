package com.inFlow.moneyManager.presentation.addCategory.model

sealed class AddCategoryUiState {
    abstract val uiModel: AddCategoryUiModel

    data class Idle(override val uiModel: AddCategoryUiModel = AddCategoryUiModel()) :
        AddCategoryUiState()

    data class Error(override val uiModel: AddCategoryUiModel) : AddCategoryUiState()
}
