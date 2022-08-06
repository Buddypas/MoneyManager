package com.inFlow.moneyManager.domain.category.usecase

import com.inFlow.moneyManager.domain.SuspendingNonParameterUseCase
import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.domain.category.repository.CategoryRepository

class GetExpenseCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) : SuspendingNonParameterUseCase<List<Category>> {
    override suspend fun execute(): List<Category> = categoryRepository.getAllExpenseCategories()
}
