package com.inFlow.moneyManager.data.mapper

import com.inFlow.moneyManager.data.db.entities.CategoryDto
import com.inFlow.moneyManager.domain.SuspendingMapper
import com.inFlow.moneyManager.domain.category.Category
import com.inFlow.moneyManager.presentation.addTransaction.model.CategoryType
import kotlinx.coroutines.CoroutineDispatcher

class CategoryDtoToCategoryMapper(
    defaultDispatcher: CoroutineDispatcher
) : SuspendingMapper<CategoryDto, Category>(defaultDispatcher) {

    override suspend fun CategoryDto.toMappedEntity(): Category =
        Category(
            id = categoryId,
            name = categoryName,
            type = categoryType.resolveCategoryType(),
            iconUrl = categoryIconUrl
        )

    private fun String.resolveCategoryType() =
        if (this == "expense") CategoryType.EXPENSE
        else CategoryType.INCOME
}
