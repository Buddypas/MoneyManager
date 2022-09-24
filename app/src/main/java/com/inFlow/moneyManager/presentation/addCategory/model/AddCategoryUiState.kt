package com.inFlow.moneyManager.presentation.addCategory.model

import com.inFlow.moneyManager.shared.base.UiState

sealed class AddCategoryUiState : UiState<AddCategoryUiModel>() {
    data class Idle(override val uiModel: AddCategoryUiModel = AddCategoryUiModel()) :
        AddCategoryUiState()

    data class Error(override val uiModel: AddCategoryUiModel) : AddCategoryUiState()
}
