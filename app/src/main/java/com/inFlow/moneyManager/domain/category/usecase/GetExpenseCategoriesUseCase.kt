package com.inFlow.moneyManager.domain.category.usecase

import com.inFlow.moneyManager.domain.SuspendingNonParameterUseCase
import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.domain.category.repository.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetExpenseCategoriesUseCase(
    private val categoryRepository: CategoryRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SuspendingNonParameterUseCase<List<Category>> {
    override suspend fun execute(): List<Category> = withContext(ioDispatcher) {
        categoryRepository.getAllExpenseCategories()
    }
}
