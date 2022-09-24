package com.inFlow.moneyManager.data.extension

import com.inFlow.moneyManager.data.db.entity.CategoryDto
import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType

fun CategoryDto.toCategory(): Category =
    Category(
        id = categoryId,
        name = categoryName,
        type = CategoryType.fromRawValue(categoryType)
    )