package com.inFlow.moneyManager.presentation.addCategory.model

import com.inFlow.moneyManager.db.entities.Category

data class Categories(val expenses: List<Category>, val incomes: List<Category>)
