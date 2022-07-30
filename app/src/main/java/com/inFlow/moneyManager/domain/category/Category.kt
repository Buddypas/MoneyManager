package com.inFlow.moneyManager.domain.category

import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType

data class Category(
    val id: Int,
    val name: String,
    val type: CategoryType,
    val iconUrl: String?
)
