package com.inFlow.moneyManager.repository

import com.inFlow.moneyManager.db.AppDatabase
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.*

class AppRepository(val db: AppDatabase) {

    fun populateDb(scope: CoroutineScope) = scope.launch {
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
        db.transactionsDao().insertAll(
            Transaction(
                0,
                50.0,
                Date.from(Instant.now()),
                "Pregled specijaliste",
                2,
                -50.0
            ),
            Transaction(
                0,
                650.0,
                Date.from(Instant.now()),
                "Plata",
                3,
                600.0
            ),
            Transaction(
                0,
                50.0,
                Date.from(Instant.now()),
                "Gorivo",
                1,
                550.0
            ),
        )
    }

    fun getAllTransactions() = db.transactionsDao().getAll()
}