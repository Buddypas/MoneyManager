package com.inFlow.moneyManager.domain.category.model

import android.os.Parcelable
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Int = 0,
    val name: String,
    val type: CategoryType
) : Parcelable
