package com.inFlow.moneyManager.presentation.addTransaction.model

sealed class AddTransactionUiEvent {
    data class ShowErrorMessage(val msgResId: Int) : AddTransactionUiEvent()
    data class ShowSuccessMessage(val msgResId: Int) : AddTransactionUiEvent()
    object NavigateUp : AddTransactionUiEvent()
}