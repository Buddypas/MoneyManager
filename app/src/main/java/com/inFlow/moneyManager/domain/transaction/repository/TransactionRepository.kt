package com.inFlow.moneyManager.domain.transaction.repository

import com.inFlow.moneyManager.domain.transaction.model.Transaction
import com.inFlow.moneyManager.presentation.dashboard.model.Filters
import java.util.*

interface TransactionRepository {
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun getTransactions(filters: Filters? = null, query: String = ""): List<Transaction>
    suspend fun searchTransactions(
        query: String,
        startDate: Date,
        endDate: Date
    ): List<Transaction>
    suspend fun getAllTransactions(): List<Transaction>
    suspend fun calculateExpensesAndIncomes(): Pair<Double, Double>
}
