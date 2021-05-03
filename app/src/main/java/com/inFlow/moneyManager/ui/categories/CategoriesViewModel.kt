package com.inFlow.moneyManager.ui.categories

import androidx.lifecycle.ViewModel
import com.inFlow.moneyManager.repository.AppRepository

class CategoriesViewModel(private val repository: AppRepository) : ViewModel() {
    val categoryList = repository.getAllCategories()
}