package com.inFlow.moneyManager.presentation.dashboard.extensions

import com.inFlow.moneyManager.presentation.dashboard.model.DashboardUiModel
import com.inFlow.moneyManager.presentation.dashboard.model.DashboardUiState

fun DashboardUiState.updateWith(updatedModel: DashboardUiModel) =
    when (this) {
        is DashboardUiState.Idle -> DashboardUiState.Idle(updatedModel)
        is DashboardUiState.Loading -> DashboardUiState.Loading(updatedModel)
    }
