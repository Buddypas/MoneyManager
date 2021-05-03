package com.inFlow.moneyManager.repository

import androidx.room.withTransaction
import com.inFlow.moneyManager.db.AppDatabase
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction
import com.inFlow.moneyManager.shared.base.Resource
import com.inFlow.moneyManager.shared.kotlin.toDate
import com.inFlow.moneyManager.ui.add_transaction.CategoryType
import com.inFlow.moneyManager.ui.dashboard.PeriodMode
import com.inFlow.moneyManager.ui.dashboard.ShowTransactions
import com.inFlow.moneyManager.ui.dashboard.SortBy
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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

    fun getTransactions(filters: FiltersDto? = null): Flow<List<Transaction>> {
        if (filters == null) return db.transactionsDao().getAll()
        val startDate: Date
        val endDate: Date
        if (filters.period == PeriodMode.WHOLE_MONTH) {
            startDate = filters.yearMonth!!.atDay(1).toDate()
            endDate = filters.yearMonth!!.atEndOfMonth().toDate()
        } else {
            startDate = filters.customRange.first!!.toDate()
            endDate = filters.customRange.second!!.toDate()
        }
        if (filters.searchQuery.isNotBlank()) return db.transactionsDao()
            .searchTransactions(filters.searchQuery, startDate, endDate)

        when (filters.show) {
            ShowTransactions.SHOW_EXPENSES ->
                return when (filters.sortBy) {
                    SortBy.SORT_BY_DATE -> {
                        if (filters.isDescending) db.transactionsDao()
                            .getAllExpensesSortedByDateDescending(startDate, endDate)
                        else db.transactionsDao()
                            .getAllExpensesSortedByDateAscending(startDate, endDate)
                    }
                    SortBy.SORT_BY_CATEGORY -> {
                        if (filters.isDescending) db.transactionsDao()
                            .getAllExpensesSortedByCategoryDescending(startDate, endDate)
                        else db.transactionsDao()
                            .getAllExpensesSortedByCategoryAscending(startDate, endDate)
                    }
                    SortBy.SORT_BY_AMOUNT -> {
                        if (filters.isDescending) db.transactionsDao()
                            .getAllExpensesSortedByAmountDescending(startDate, endDate)
                        else db.transactionsDao()
                            .getAllExpensesSortedByAmountAscending(startDate, endDate)
                    }
                }
            ShowTransactions.SHOW_INCOMES ->
                return when (filters.sortBy) {
                    SortBy.SORT_BY_DATE -> {
                        if (filters.isDescending) db.transactionsDao()
                            .getAllIncomesSortedByDateDescending(startDate, endDate)
                        else db.transactionsDao()
                            .getAllIncomesSortedByDateAscending(startDate, endDate)
                    }
                    SortBy.SORT_BY_CATEGORY -> {
                        if (filters.isDescending) db.transactionsDao()
                            .getAllIncomesSortedByCategoryDescending(startDate, endDate)
                        else db.transactionsDao()
                            .getAllIncomesSortedByCategoryAscending(startDate, endDate)
                    }
                    SortBy.SORT_BY_AMOUNT -> {
                        if (filters.isDescending) db.transactionsDao()
                            .getAllIncomesSortedByAmountDescending(startDate, endDate)
                        else db.transactionsDao()
                            .getAllIncomesSortedByAmountAscending(startDate, endDate)
                    }
                }
            else ->
                return when (filters.sortBy) {
                    SortBy.SORT_BY_DATE -> {
                        if (filters.isDescending) db.transactionsDao()
                            .getAllTransactionsSortedByDateDescending(startDate, endDate)
                        else db.transactionsDao()
                            .getAllTransactionsSortedByDateAscending(startDate, endDate)
                    }
                    SortBy.SORT_BY_CATEGORY -> {
                        if (filters.isDescending) db.transactionsDao()
                            .getAllTransactionsSortedByCategoryDescending(startDate, endDate)
                        else db.transactionsDao()
                            .getAllTransactionsSortedByCategoryAscending(startDate, endDate)
                    }
                    SortBy.SORT_BY_AMOUNT -> {
                        if (filters.isDescending) db.transactionsDao()
                            .getAllTransactionsSortedByAmountDescending(startDate, endDate)
                        else db.transactionsDao()
                            .getAllTransactionsSortedByAmountAscending(startDate, endDate)
                    }
                }
        }
    }

    suspend fun getAllExpenses() = db.transactionsDao().getExpenses()
    suspend fun getAllIncomes() = db.transactionsDao().getIncomes()
    fun getAllCategories() = db.categoriesDao().getAll()
    fun getAllExpenseCategories() = db.categoriesDao().getAllExpenseCategories()
    fun getAllIncomeCategories() = db.categoriesDao().getAllIncomeCategories()
}