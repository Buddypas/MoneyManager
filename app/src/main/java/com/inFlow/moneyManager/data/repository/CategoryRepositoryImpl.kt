package com.inFlow.moneyManager.data.repository

import com.inFlow.moneyManager.data.db.MoneyManagerDatabase
import com.inFlow.moneyManager.data.db.entity.CategoryDto
import com.inFlow.moneyManager.data.mapper.CategoryDtoToCategoryMapper
import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.domain.category.repository.CategoryRepository
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val db: MoneyManagerDatabase,
    private val categoryDtoToCategoryMapper: CategoryDtoToCategoryMapper
) : CategoryRepository {

    private suspend fun populateCategories() {
        db.categoriesDao().insertAll(
            CategoryDto(
                categoryName = "Car",
                categoryType = "expense"
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

    override suspend fun saveCategory(type: CategoryType, name: String) =
        withContext(Dispatchers.IO) {
            db.categoriesDao().insertAll(
                CategoryDto(
                    categoryName = name,
                    categoryType = if (type == CategoryType.EXPENSE) "expense" else "income"
                )
            )
        }

    override suspend fun getAllCategories(): List<Category> =
        runCatching {
            db.categoriesDao().getAll()
        }.mapCatching {
            categoryDtoToCategoryMapper.mapList(it)
        }.getOrThrow()

    override suspend fun getAllExpenseCategories(): List<CategoryDto> =
        db.categoriesDao().getAllExpenseCategories()

    override suspend fun getAllIncomeCategories(): List<CategoryDto> =
        db.categoriesDao().getAllIncomeCategories()
}
