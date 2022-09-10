package com.inFlow.moneyManager.presentation.addTransaction.model

import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.presentation.shared.FieldError
import java.time.LocalDate

data class AddTransactionUiModel(
    val categoryType: CategoryType = CategoryType.EXPENSE,
    val expenseList: List<Category>? = null,
    val incomeList: List<Category>? = null,
    val activeCategoryList: List<Category>? = null,
    val selectedCategory: Category? = null,
    val selectedDescription: String? = null,
    val selectedAmount: Double? = null,
    val selectedDate: LocalDate? = null,
    val fieldError: FieldError? = null
)
