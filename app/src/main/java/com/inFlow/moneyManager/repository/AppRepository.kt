package com.inFlow.moneyManager.repository

import androidx.room.withTransaction
import com.inFlow.moneyManager.db.AppDatabase
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction
import com.inFlow.moneyManager.shared.base.Resource
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
//        delay(500)
//        var now = Date.from(Instant.now())
//        db.transactionsDao().insertAll(
//            Transaction(
//                0,
//                -50.0,
//                now,
//                "Pregled specijaliste",
//                2,
//                -50.0
//            )
//        )
//        delay(500)
//        now = Date.from(Instant.now())
//        db.transactionsDao().insertAll(
//            Transaction(
//                0,
//                650.0,
//                now,
//                "Plata",
//                3,
//                600.0
//            )
//        )
//        now = Date.from(Instant.now())
//        db.transactionsDao().insertAll(
//            Transaction(
//                0,
//                -50.0,
//                now,
//                "Gorivo",
//                1,
//                550.0
//            )
//        )
    }

    suspend fun saveTransaction(amount: Double, categoryId: Int, desc: String) {
//        emit(Resource.Loading())
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
//        db.transactionsDao().fetchLastAndInsertNewTransaction(amount, categoryId, desc)
//        val lastTransaction = db.transactionsDao().getMostRecentTransaction()
//        val previousBalance = lastTransaction.transactionBalanceAfter
//        val transaction = Transaction(
//            transactionAmount = amount,
//            transactionDate = Date(),
//            transactionDescription = desc.trim(),
//            transactionCategoryId = categoryId,
//            transactionBalanceAfter = previousBalance + amount
//        )
//        db.transactionsDao().insertAll(transaction)
    }

    fun getAllTransactions() = db.transactionsDao().getAll()
    fun getAllExpenseCategories() = db.categoriesDao().getAllExpenseCategories()
    fun getAllIncomeCategories() = db.categoriesDao().getAllIncomeCategories()
}