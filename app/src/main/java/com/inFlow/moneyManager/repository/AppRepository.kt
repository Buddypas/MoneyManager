package com.inFlow.moneyManager.repository

import com.inFlow.moneyManager.db.AppDatabase
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.*

class AppRepository(val db: AppDatabase) {

    suspend fun populateDb() {
        db.categoriesDao().insertAll(
            Category(
                0,
                "Car",
                "expense",
            ),
            Category(
                0,
                "Health",
                "expense"
            ),
            Category(
                0,
                "Salary",
                "income"
            )
        )
        delay(500)
        var now = Date.from(Instant.now())
        db.transactionsDao().insertAll(
            Transaction(
                0,
                -50.0,
                now,
                "Pregled specijaliste",
                2,
                -50.0
            )
        )
        delay(500)
        now = Date.from(Instant.now())
        db.transactionsDao().insertAll(
            Transaction(
                0,
                650.0,
                now,
                "Plata",
                3,
                600.0
            )
        )
        now = Date.from(Instant.now())
        db.transactionsDao().insertAll(
            Transaction(
                0,
                -50.0,
                now,
                "Gorivo",
                1,
                550.0
            )
        )
    }

    suspend fun saveTransaction(amount: Double, categoryId: Int, desc: String) {
        val lastTransaction = db.transactionsDao().getMostRecentTransaction()
        val previousBalance = lastTransaction.transactionBalanceAfter
        val transaction = Transaction(
            transactionAmount = amount,
            transactionDate = Date(),
            transactionDescription = desc.trim(),
            transactionCategoryId = categoryId,
            transactionBalanceAfter = previousBalance + amount
        )
        db.transactionsDao().insertAll(transaction)
    }

    fun getAllTransactions() = db.transactionsDao().getAll()
    fun getAllExpenseCategories() = db.categoriesDao().getAllExpenseCategories()
    fun getAllIncomeCategories() = db.categoriesDao().getAllIncomeCategories()
}