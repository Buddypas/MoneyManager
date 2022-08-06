package com.inFlow.moneyManager.presentation.filters.extension

import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiState

fun FiltersUiState.updateWith(updatedModel: FiltersUiModel) =
    when (this) {
        is FiltersUiState.Idle -> FiltersUiState.Idle(updatedModel)
        is FiltersUiState.Error -> FiltersUiState.Error(updatedModel)
    }
