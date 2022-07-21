package com.inFlow.moneyManager.presentation.addTransaction.extension

import com.inFlow.moneyManager.presentation.addCategory.model.Categories
import com.inFlow.moneyManager.presentation.addTransaction.model.AddTransactionUiModel
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType

fun AddTransactionUiModel.inferCategoryType(
    categories: Categories
) =
    if (categoryType == CategoryType.EXPENSE) categories.expenses
    else categories.incomes

fun AddTransactionUiModel.updateCategoryType(isExpenseChecked: Boolean): AddTransactionUiModel {
    val newCategoryType =
        if (isExpenseChecked) CategoryType.EXPENSE
        else CategoryType.INCOME
    val newCategoryList =
        if (newCategoryType == CategoryType.EXPENSE)
            expenseList
        else incomeList
    return this.copy(
        selectedCategory = null,
        categoryType = newCategoryType,
        activeCategoryList = newCategoryList
    )
}
