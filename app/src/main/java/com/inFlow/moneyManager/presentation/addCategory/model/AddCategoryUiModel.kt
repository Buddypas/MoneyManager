package com.inFlow.moneyManager.presentation.addCategory.model

import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType

data class AddCategoryUiModel(
    val categoryId: Int = 0,
    val categoryName: String = "",
    val categoryType: CategoryType = CategoryType.EXPENSE,
    val errorMessageResId: Int = 0
) {
    fun isUpdate(): Boolean = categoryId > 0
}
