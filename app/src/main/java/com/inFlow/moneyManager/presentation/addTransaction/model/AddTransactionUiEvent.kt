package com.inFlow.moneyManager.presentation.addTransaction.model

sealed class AddTransactionUiEvent {
    data class ShowErrorMessage(val msgResId: Int) : AddTransactionUiEvent()
    data class ShowSuccessMessage(val msg: String) : AddTransactionUiEvent()
    object NavigateUp : AddTransactionUiEvent()
}