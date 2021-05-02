package com.inFlow.moneyManager.ui.filters

import androidx.lifecycle.ViewModel
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.vo.FiltersDto

class FiltersViewModel(private val repository: AppRepository) : ViewModel()  {
    var filters: FiltersDto? = null


}