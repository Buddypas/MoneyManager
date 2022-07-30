package com.inFlow.moneyManager.presentation.dashboard.model

fun DashboardUiState.updateWith(updatedModel: DashboardUiModel) =
    when (this) {
        is DashboardUiState.Idle -> DashboardUiState.Idle(updatedModel)
        is DashboardUiState.Loading -> DashboardUiState.Loading(updatedModel)
    }
