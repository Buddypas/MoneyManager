package com.inFlow.moneyManager.presentation.filters.model

sealed class FiltersUiState {
    abstract val uiModel: FiltersUiModel

    data class Idle(override val uiModel: FiltersUiModel = FiltersUiModel()) :
        FiltersUiState()
    data class Error(override val uiModel: FiltersUiModel) : FiltersUiState()
}
