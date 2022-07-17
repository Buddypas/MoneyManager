package com.inFlow.moneyManager.ui.categories

import androidx.lifecycle.ViewModel
import com.inFlow.moneyManager.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {
    val categoryList = repository.getAllCategories()
}