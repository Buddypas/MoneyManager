package com.inFlow.moneyManager.presentation.addCategory

import com.inFlow.moneyManager.presentation.add_transaction.CategoryType

data class AddCategoryUiModel(
    val categoryType: CategoryType = CategoryType.EXPENSE,
    val categoryName: String = "",
    val errorMessageResId: Int = 0
)