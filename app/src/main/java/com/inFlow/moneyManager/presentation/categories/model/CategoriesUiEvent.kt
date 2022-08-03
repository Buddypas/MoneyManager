package com.inFlow.moneyManager.presentation.categories.model

import com.inFlow.moneyManager.data.db.entity.CategoryDto

sealed class CategoriesUiEvent {
    data class GoToCategory(val category: CategoryDto) : CategoriesUiEvent()
}