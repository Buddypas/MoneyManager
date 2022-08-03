package com.inFlow.moneyManager.presentation.addCategory.model

import com.inFlow.moneyManager.data.db.entity.CategoryDto

data class Categories(val expenses: List<CategoryDto>, val incomes: List<CategoryDto>)
