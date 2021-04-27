package com.inFlow.moneyManager.repository

import androidx.room.withTransaction
import com.inFlow.moneyManager.db.AppDatabase
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction
import com.inFlow.moneyManager.shared.base.Resource
import com.inFlow.moneyManager.ui.add_transaction.CategoryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.*

class AppRepository(val db: AppDatabase) {

    suspend fun populateDb() {
        db.categoriesDao().insertAll(
            Category(
                categoryName = "Car",
                categoryType = "expense",
            ),
            Category(
                categoryName = "Health",
                categoryType = "expense"
            ),
            Category(
                categoryName = "Salary",
                categoryType = "income"
            )
        )
    }

    suspend fun saveTransaction(amount: Double, categoryId: Int, desc: String) {
        db.withTransaction {
            val lastTransaction = db.transactionsDao().getMostRecentTransaction()
            val previousBalance: Double = lastTransaction?.transactionBalanceAfter ?: 0.0
            val transaction = Transaction(
                transactionAmount = amount,
                transactionDate = Date(),
                transactionDescription = desc.trim(),
                transactionCategoryId = categoryId,
                transactionBalanceAfter = previousBalance + amount
            )
            db.transactionsDao().insertAll(transaction)
        }
    }

    suspend fun saveCategory(type: CategoryType, name: String) = db.categoriesDao().insertAll(
        Category(
            categoryName = name,
            categoryType = if (type == CategoryType.EXPENSE) "expense" else "income"
        )
    )

    fun getAllTransactions() = db.transactionsDao().getAll()
    suspend fun getAllExpenses() = db.transactionsDao().getExpenses()
    suspend fun getAllIncomes() = db.transactionsDao().getIncomes()
    fun getAllExpenseCategories() = db.categoriesDao().getAllExpenseCategories()
    fun getAllIncomeCategories() = db.categoriesDao().getAllIncomeCategories()
}