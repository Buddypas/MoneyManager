package com.inFlow.moneyManager.presentation.categories.model

import com.inFlow.moneyManager.domain.category.model.Category

sealed class CategoriesUiEvent {
    data class GoToCategory(val category: Category) : CategoriesUiEvent()
}
