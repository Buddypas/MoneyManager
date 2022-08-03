package com.inFlow.moneyManager.data.mapper

import com.inFlow.moneyManager.data.db.entity.CategoryDto
import com.inFlow.moneyManager.domain.SuspendingMapper
import com.inFlow.moneyManager.domain.category.model.Category
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CategoryDtoToCategoryMapper(
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SuspendingMapper<CategoryDto, Category>(defaultDispatcher) {

    override suspend fun CategoryDto.toMappedEntity(): Category =
        Category(
            id = categoryId,
            name = categoryName,
            type = categoryType.resolveCategoryType(),
        )

    private fun String.resolveCategoryType() =
        if (this == "expense") CategoryType.EXPENSE
        else CategoryType.INCOME
}
