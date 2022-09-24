package com.inFlow.moneyManager.data.repository

import com.inFlow.moneyManager.data.db.MoneyManagerDatabase
import com.inFlow.moneyManager.data.db.entity.CategoryDto
import com.inFlow.moneyManager.data.extension.toCategory
import com.inFlow.moneyManager.data.extension.toCategoryDto
import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.domain.category.repository.CategoryRepository
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val db: MoneyManagerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryRepository {

    override suspend fun saveCategory(type: CategoryType, name: String) =
        withContext(ioDispatcher) {
            db.categoriesDao().insertAll(
                CategoryDto(
                    categoryName = name,
                    categoryType = if (type == CategoryType.EXPENSE) "expense" else "income"
                )
            )
        }

    override suspend fun updateCategory(updatedCategory: Category) =
        withContext(ioDispatcher) {
            runCatching {
                updatedCategory.toCategoryDto()
            }.mapCatching {
                db.categoriesDao().updateCategories(it)
            }.getOrThrow()
        }

    override suspend fun doesCategoryExist(categoryId: Int): Boolean =
        withContext(ioDispatcher) {
            runCatching {
                db.categoriesDao().getById(categoryId)
            }.mapCatching {
                it != null
            }.getOrDefault(false)
        }

    override suspend fun getAllCategories(): List<Category> =
        withContext(ioDispatcher) {
            runCatching {
                db.categoriesDao().getAll()
            }.mapCatching { categoryDtos ->
                categoryDtos.map { it.toCategory() }
            }.getOrThrow()
        }

    override suspend fun getAllExpenseCategories(): List<Category> =
        withContext(ioDispatcher) {
            runCatching {
                db.categoriesDao().getAllExpenseCategories()
            }.mapCatching { categoryDtos ->
                categoryDtos.map { it.toCategory() }
            }.getOrThrow()
        }

    override suspend fun getAllIncomeCategories(): List<Category> =
        withContext(ioDispatcher) {
            runCatching {
                db.categoriesDao().getAllIncomeCategories()
            }.mapCatching { categoryDtos ->
                categoryDtos.map { it.toCategory() }
            }.getOrThrow()
        }
}
