package com.inFlow.moneyManager.presentation.filters.model

sealed class FiltersUiEvent {
    data class ApplyFilters(val filtersData: FiltersUiModel) : FiltersUiEvent()
}