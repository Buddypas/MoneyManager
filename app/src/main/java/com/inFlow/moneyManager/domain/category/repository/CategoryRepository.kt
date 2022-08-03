package com.inFlow.moneyManager.domain.category.repository

import com.inFlow.moneyManager.data.db.entity.CategoryDto
import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType

interface CategoryRepository {
    suspend fun saveCategory(type: CategoryType, name: String)
    suspend fun getAllCategories(): List<Category>
    suspend fun getAllExpenseCategories(): List<CategoryDto>
    suspend fun getAllIncomeCategories(): List<CategoryDto>
}
