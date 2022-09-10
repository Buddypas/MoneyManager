package com.inFlow.moneyManager.domain.transaction.repository

import com.inFlow.moneyManager.domain.transaction.model.BalanceData
import com.inFlow.moneyManager.domain.transaction.model.Transaction
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel
import java.util.*

interface TransactionRepository {
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun getTransactions(filters: FiltersUiModel? = null, query: String = ""): List<Transaction>
    suspend fun searchTransactions(
        query: String,
        startDate: Date,
        endDate: Date
    ): List<Transaction>
    suspend fun getAllTransactions(): List<Transaction>
    suspend fun calculateExpensesAndIncomes(): BalanceData
}
