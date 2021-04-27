package com.inFlow.moneyManager.ui.add_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.ui.add_transaction.CategoryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCategoryViewModel(private val repository: AppRepository) : ViewModel() {
    var categoryType = CategoryType.EXPENSE

    fun saveCategory(name: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveCategory(categoryType, name)
    }
}