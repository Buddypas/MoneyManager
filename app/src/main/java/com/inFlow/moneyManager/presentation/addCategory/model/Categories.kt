package com.inFlow.moneyManager.presentation.addCategory.model

import com.inFlow.moneyManager.domain.category.model.Category

data class Categories(val expenses: List<Category>, val incomes: List<Category>)
