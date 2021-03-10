package com.inFlow.moneyManager.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class DashboardViewModel() : ViewModel() {
    val searchQuery = MutableStateFlow<String>("")
}