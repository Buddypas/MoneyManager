package com.inFlow.moneyManager.presentation.dashboard.model

import com.inFlow.moneyManager.domain.transaction.model.Transaction
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel

data class DashboardUiModel(
    val query: String = "",
    val transactionList: List<Transaction>? = null,
    val filters: FiltersUiModel = FiltersUiModel(),
    val income: Double = 0.0,
    val expenses: Double = 0.0
)
