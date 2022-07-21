package com.inFlow.moneyManager.presentation.categories.model

import com.inFlow.moneyManager.data.db.entities.CategoryDto

data class CategoriesUiModel(
    val categoryList: List<CategoryDto>? = null
)
