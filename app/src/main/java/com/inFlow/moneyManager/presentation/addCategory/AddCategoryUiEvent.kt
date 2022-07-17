package com.inFlow.moneyManager.presentation.addCategory

sealed class AddCategoryUiEvent {
    data class ShowErrorMessage(val msg: String?) : AddCategoryUiEvent()
    data class ShowSuccessMessage(val msg: String) : AddCategoryUiEvent()
    object NavigateUp : AddCategoryUiEvent()
}