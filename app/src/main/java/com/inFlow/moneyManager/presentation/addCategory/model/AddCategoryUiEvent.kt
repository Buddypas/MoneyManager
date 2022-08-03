package com.inFlow.moneyManager.presentation.addCategory.model

import androidx.annotation.StringRes

sealed class AddCategoryUiEvent {
    data class ShowErrorMessage(@StringRes val msgResId: Int) : AddCategoryUiEvent()
    data class ShowMessage(@StringRes val msgResId: Int) : AddCategoryUiEvent()
    object NavigateUp : AddCategoryUiEvent()
}