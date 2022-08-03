package com.inFlow.moneyManager.domain.category.model

import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType

data class Category(
    val id: Int = 0,
    val name: String,
    val type: CategoryType
)
