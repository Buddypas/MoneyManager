package com.inFlow.moneyManager.presentation.dashboard.model

sealed class DashboardUiEvent {
    object NavigateToAddTransaction : DashboardUiEvent()
    data class OpenFilters(val filters: Filters) : DashboardUiEvent()
}