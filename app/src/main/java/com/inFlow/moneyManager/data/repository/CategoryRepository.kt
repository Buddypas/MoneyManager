package com.inFlow.moneyManager.data.repository

import com.inFlow.moneyManager.data.db.MoneyManagerDatabase
import com.inFlow.moneyManager.data.db.entities.CategoryDto
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val db: MoneyManagerDatabase) {
    suspend fun populateCategories() {
        db.categoriesDao().insertAll(
            CategoryDto(
                categoryName = "Car",
                categoryType = "expense",
            ),
            CategoryDto(
                categoryName = "Health",
                categoryType = "expense"
            ),
            CategoryDto(
                categoryName = "Salary",
                categoryType = "income"
            )
        )
    }

    suspend fun saveCategory(type: CategoryType, name: String) = withContext(Dispatchers.IO) {
        db.categoriesDao().insertAll(
            CategoryDto(
                categoryName = name,
                categoryType = if (type == CategoryType.EXPENSE) "expense" else "income"
            )
        )
    }

    fun getAllCategories(): Flow<List<CategoryDto>> = db.categoriesDao().getAll()
    suspend fun getAllExpenseCategories() = db.categoriesDao().getAllExpenseCategories()
    suspend fun getAllIncomeCategories() = db.categoriesDao().getAllIncomeCategories()

}
