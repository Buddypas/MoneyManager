package com.inFlow.moneyManager.presentation.addCategory.model

sealed class AddCategoryUiEvent {
    data class ShowErrorMessage(val msg: String?) : AddCategoryUiEvent()
    data class ShowSuccessMessage(val msg: String) : AddCategoryUiEvent()
    object NavigateUp : AddCategoryUiEvent()
}