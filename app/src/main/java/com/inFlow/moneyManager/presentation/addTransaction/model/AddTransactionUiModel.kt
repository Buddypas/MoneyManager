package com.inFlow.moneyManager.presentation.addTransaction.model

import com.inFlow.moneyManager.domain.category.model.Category

data class AddTransactionUiModel(
    val categoryType: CategoryType = CategoryType.EXPENSE,
    val expenseList: List<Category>? = null,
    val incomeList: List<Category>? = null,
    val activeCategoryList: List<Category>? = null,
    val selectedCategory: Category? = null,
    val categoryErrorResId: Int? = null,
    val descriptionErrorResId: Int? = null,
    val amountErrorResId: Int? = null
)
