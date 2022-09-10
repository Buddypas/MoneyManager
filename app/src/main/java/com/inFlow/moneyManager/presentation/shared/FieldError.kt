package com.inFlow.moneyManager.presentation.shared

import android.os.Parcelable
import androidx.annotation.StringRes
import com.inFlow.moneyManager.shared.kotlin.FieldType
import kotlinx.parcelize.Parcelize

@Parcelize
data class FieldError(val fieldType: FieldType, @StringRes val errorResId: Int): Parcelable
