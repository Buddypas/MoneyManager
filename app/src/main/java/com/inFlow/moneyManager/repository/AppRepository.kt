package com.inFlow.moneyManager.repository

import com.inFlow.moneyManager.db.AppDatabase
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction
import com.inFlow.moneyManager.shared.kotlin.toDate
import com.inFlow.moneyManager.presentation.addTransaction.CategoryType
import com.inFlow.moneyManager.presentation.dashboard.PeriodMode
import com.inFlow.moneyManager.presentation.dashboard.ShowTransactions
import com.inFlow.moneyManager.presentation.dashboard.SortBy
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(private val db: AppDatabase) {

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
        db.transactionsDao().saveTransaction(amount, categoryId, desc)
    }

    suspend fun saveCategory(type: CategoryType, name: String) = db.categoriesDao().insertAll(
        Category(
            categoryName = name,
            categoryType = if (type == CategoryType.EXPENSE) "expense" else "income"
        )
    )

    fun getTransactions(filters: FiltersDto? = null, query: String = ""): Flow<List<Transaction>> {
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
        if (query.isNotBlank())
            return db.transactionsDao().searchTransactions(query, startDate, endDate)

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

    fun getAll() = db.transactionsDao().getAll()

    suspend fun getAllExpenses() = db.transactionsDao().getExpenses()
    suspend fun getAllIncomes() = db.transactionsDao().getIncomes()
    fun getAllCategories() = db.categoriesDao().getAll()
    suspend fun getAllExpenseCategories() = db.categoriesDao().getAllExpenseCategories()
    suspend fun getAllIncomeCategories() = db.categoriesDao().getAllIncomeCategories()
}