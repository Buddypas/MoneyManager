package com.inFlow.moneyManager.presentation.dashboard.model

import android.os.Parcelable
import com.inFlow.moneyManager.shared.kotlin.FieldType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FieldError(val message: String, val field: FieldType) : Parcelable
