package com.inFlow.moneyManager.presentation.addCategory

fun AddCategoryUiState.updateWith(updatedModel: AddCategoryUiModel) =
    when (this) {
        is AddCategoryUiState.Idle -> AddCategoryUiState.Idle(updatedModel)
        is AddCategoryUiState.Error -> AddCategoryUiState.Error(updatedModel)
    }
