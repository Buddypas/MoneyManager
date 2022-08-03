package com.inFlow.moneyManager.presentation.addTransaction.model

import com.inFlow.moneyManager.data.db.entity.CategoryDto

data class AddTransactionUiModel(
    val categoryType: CategoryType = CategoryType.EXPENSE,
    val expenseList: List<CategoryDto>? = null,
    val incomeList: List<CategoryDto>? = null,
    val activeCategoryList: List<CategoryDto>? = null,
    val selectedCategory: CategoryDto? = null,
    val categoryErrorResId: Int? = null,
    val descriptionErrorResId: Int? = null,
    val amountErrorResId: Int? = null
)
