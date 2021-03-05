package com.inFlow.moneyManager.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel() : ViewModel() {
    val searchQuery = MutableLiveData<String>()
}