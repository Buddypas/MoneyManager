package com.inFlow.moneyManager.domain.category.usecase

import com.inFlow.moneyManager.domain.SuspendingUseCase
import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.domain.category.repository.CategoryRepository

class SaveCategoryUseCase(
    private val categoryRepository: CategoryRepository
) : SuspendingUseCase<Category, Unit> {
    override suspend fun execute(parameter: Category) {
        categoryRepository.saveCategory(parameter.type, parameter.name)
    }
}
