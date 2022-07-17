package com.inFlow.moneyManager.presentation.addCategory.extension

import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiModel
import com.inFlow.moneyManager.presentation.addCategory.model.AddCategoryUiState

fun AddCategoryUiState.updateWith(updatedModel: AddCategoryUiModel) =
    when (this) {
        is AddCategoryUiState.Idle -> AddCategoryUiState.Idle(updatedModel)
        is AddCategoryUiState.Error -> AddCategoryUiState.Error(updatedModel)
    }
