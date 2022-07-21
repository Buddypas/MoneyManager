package com.inFlow.moneyManager.presentation.categories.model

sealed class CategoriesUiState {
    abstract val uiModel: CategoriesUiModel

    data class Idle(override val uiModel: CategoriesUiModel = CategoriesUiModel()) :
        CategoriesUiState()
    data class Loading(override val uiModel: CategoriesUiModel = CategoriesUiModel()) :
        CategoriesUiState()
}
