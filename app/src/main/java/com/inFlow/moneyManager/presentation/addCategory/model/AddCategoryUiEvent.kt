package com.inFlow.moneyManager.presentation.addCategory.model

import androidx.annotation.StringRes
import com.inFlow.moneyManager.shared.base.UiEvent

sealed class AddCategoryUiEvent : UiEvent {
    data class ShowErrorMessage(@StringRes val msgResId: Int) : AddCategoryUiEvent()
    data class ShowMessage(@StringRes val msgResId: Int) : AddCategoryUiEvent()
    object NavigateUp : AddCategoryUiEvent()
}
