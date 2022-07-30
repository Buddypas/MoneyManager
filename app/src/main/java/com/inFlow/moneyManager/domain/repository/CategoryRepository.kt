package com.inFlow.moneyManager.domain.repository

import com.inFlow.moneyManager.data.db.entities.CategoryDto
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun saveCategory(type: CategoryType, name: String)
    fun getAllCategories(): Flow<List<CategoryDto>>
    suspend fun getAllExpenseCategories(): List<CategoryDto>
    suspend fun getAllIncomeCategories(): List<CategoryDto>
}
