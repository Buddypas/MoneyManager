package com.inFlow.moneyManager.presentation.filters.model

sealed class FiltersUiEvent {
    data class NavigateUp(val newFilters: FiltersUiModel) : FiltersUiEvent()
}
