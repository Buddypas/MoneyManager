package com.inFlow.moneyManager.presentation.categories.model

import com.inFlow.moneyManager.domain.category.model.Category

data class CategoriesUiModel(
    val categoryList: List<Category>? = null
)
