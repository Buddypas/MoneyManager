package com.inFlow.moneyManager.presentation.categories.model

import com.inFlow.moneyManager.data.db.entities.CategoryDto

sealed class CategoriesUiEvent {
    data class GoToCategory(val category: CategoryDto) : CategoriesUiEvent()
}