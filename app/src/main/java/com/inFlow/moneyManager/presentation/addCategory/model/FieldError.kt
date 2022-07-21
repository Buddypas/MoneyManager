package com.inFlow.moneyManager.presentation.addCategory.model

import androidx.annotation.StringRes

data class FieldError(val fieldType: FieldType, @StringRes val errorResId: Int)
