package com.inFlow.moneyManager.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.inFlow.moneyManager.repository.AppRepository

class CategoriesViewModel(private val repository: AppRepository) : ViewModel() {

    val categoryList = repository.getAllCategories()

//    val categories = categoryList.asLiveData()

}