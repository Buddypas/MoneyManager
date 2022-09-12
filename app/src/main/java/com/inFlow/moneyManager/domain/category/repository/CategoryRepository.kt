package com.inFlow.moneyManager.domain.category.repository

import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType

interface CategoryRepository {
    suspend fun saveCategory(type: CategoryType, name: String)
    suspend fun editCategory(updatedCategory: Category)
    suspend fun getAllCategories(): List<Category>
    suspend fun getAllExpenseCategories(): List<Category>
    suspend fun getAllIncomeCategories(): List<Category>
}
