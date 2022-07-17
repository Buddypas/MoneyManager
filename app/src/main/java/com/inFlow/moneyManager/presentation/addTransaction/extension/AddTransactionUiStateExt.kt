package com.inFlow.moneyManager.presentation.addTransaction.extension

import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiModel
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiState

fun AddTransactionUiState.updateWith(updatedModel: AddTransactionUiModel) =
    when (this) {
        is AddTransactionUiState.Idle -> AddTransactionUiState.Idle(updatedModel)
        is AddTransactionUiState.LoadingCategories ->
            AddTransactionUiState.LoadingCategories(updatedModel)
        is AddTransactionUiState.Error -> AddTransactionUiState.Error(updatedModel)
    }