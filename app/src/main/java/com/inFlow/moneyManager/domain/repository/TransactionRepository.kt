package com.inFlow.moneyManager.domain.repository

import com.inFlow.moneyManager.data.db.entities.TransactionDto
import com.inFlow.moneyManager.presentation.dashboard.model.Filters

interface TransactionRepository {
    suspend fun saveTransaction(amount: Double, categoryId: Int, desc: String)
    suspend fun getTransactions(filters: Filters? = null, query: String = ""): List<TransactionDto>
    suspend fun getAll(): List<TransactionDto>
    suspend fun calculateExpenses(): Double
    suspend fun calculateIncomes(): Double
}
