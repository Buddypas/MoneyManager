package com.inFlow.moneyManager.presentation.addTransaction.model

sealed class AddTransactionUiState {
    abstract val uiModel: AddTransactionUiModel

    data class Idle(override val uiModel: AddTransactionUiModel = AddTransactionUiModel()) :
        AddTransactionUiState()

    data class LoadingCategories(override val uiModel: AddTransactionUiModel = AddTransactionUiModel()) :
        AddTransactionUiState()

    data class Error(override val uiModel: AddTransactionUiModel = AddTransactionUiModel()) :
        AddTransactionUiState()
}
