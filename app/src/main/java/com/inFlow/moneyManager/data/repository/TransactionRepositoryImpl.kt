package com.inFlow.moneyManager.data.repository

import com.inFlow.moneyManager.data.db.MoneyManagerDatabase
import com.inFlow.moneyManager.data.db.entities.TransactionDto
import com.inFlow.moneyManager.domain.repository.TransactionRepository
import com.inFlow.moneyManager.presentation.dashboard.model.Filters
import com.inFlow.moneyManager.presentation.dashboard.model.PeriodMode
import com.inFlow.moneyManager.presentation.dashboard.model.ShowTransactions
import com.inFlow.moneyManager.presentation.dashboard.model.SortBy
import com.inFlow.moneyManager.shared.kotlin.toDate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(private val db: MoneyManagerDatabase) :
    TransactionRepository {
    override suspend fun saveTransaction(amount: Double, categoryId: Int, desc: String) =
        db.transactionsDao().saveTransaction(amount, categoryId, desc)

    private fun getAllTransactions(): List<TransactionDto> = db.transactionsDao().getAll()

    // TODO: Remove !!
    private fun Filters.getStartAndEndDate(): Pair<Date, Date> {
        val startDate: Date
        val endDate: Date
        if (period == PeriodMode.WHOLE_MONTH) {
            startDate = yearMonth!!.atDay(1).toDate()
            endDate = yearMonth!!.atEndOfMonth().toDate()
        } else {
            startDate = customRange.first!!.toDate()
            endDate = customRange.second!!.toDate()
        }
        return Pair(startDate, endDate)
    }

    override suspend fun getTransactions(
        filters: Filters?,
        query: String
    ): List<TransactionDto> {
        filters ?: return getAllTransactions()

        val (startDate, endDate) = filters.getStartAndEndDate()

        query.takeIf { it.isNotBlank() }?.run {
            return db.transactionsDao().searchTransactions(query, startDate, endDate)
        }

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

    override suspend fun getAll(): List<TransactionDto> = db.transactionsDao().getAll()
    override suspend fun getAllExpenses() = db.transactionsDao().getExpenses()
    override suspend fun getAllIncomes() = db.transactionsDao().getIncomes()
}
