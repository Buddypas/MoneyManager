package com.inFlow.moneyManager.presentation.dashboard.model

sealed class DashboardUiState {
    abstract val uiModel: DashboardUiModel

    data class Idle(override val uiModel: DashboardUiModel = DashboardUiModel()) :
        DashboardUiState()
    data class Loading(override val uiModel: DashboardUiModel = DashboardUiModel()) :
        DashboardUiState()
}

