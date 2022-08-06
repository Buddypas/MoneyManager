package com.inFlow.moneyManager.presentation.dashboard.model

import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel

sealed class DashboardUiEvent {
    object NavigateToAddTransaction : DashboardUiEvent()
    data class OpenFilters(val filters: FiltersUiModel) : DashboardUiEvent()
}
