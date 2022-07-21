package com.inFlow.moneyManager.presentation.categories

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {
    val categoryList = repository.getAllCategories()
}