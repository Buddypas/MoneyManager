package com.inFlow.moneyManager.presentation.dashboard.model

import com.inFlow.moneyManager.domain.transaction.model.Transaction

data class DashboardUiModel(
    val query: String = "",
    val transactionList: List<Transaction>? = null,
    val filters: Filters = Filters(),
    val income: Double = 0.0,
    val expenses: Double = 0.0
)
