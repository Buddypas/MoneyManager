package com.inFlow.moneyManager.data.extension

import com.inFlow.moneyManager.data.db.entity.CategoryDto
import com.inFlow.moneyManager.domain.category.model.Category

fun Category.toCategoryDto(): CategoryDto =
    CategoryDto(
        categoryId = id,
        categoryName = name,
        categoryType = type.rawValue
    )
