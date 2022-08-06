package com.inFlow.moneyManager.data.repository

import com.inFlow.moneyManager.data.db.MoneyManagerDatabase
import com.inFlow.moneyManager.data.mapper.TransactionDtoToTransactionMapper
import com.inFlow.moneyManager.domain.transaction.model.Transaction
import com.inFlow.moneyManager.domain.transaction.repository.TransactionRepository
import com.inFlow.moneyManager.presentation.dashboard.model.ShowTransactions
import com.inFlow.moneyManager.presentation.dashboard.model.SortBy
import com.inFlow.moneyManager.presentation.filters.extension.getStartAndEndDate
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Create filte model for each layer
@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val db: MoneyManagerDatabase,
    private val transactionDtoToTransactionMapper: TransactionDtoToTransactionMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TransactionRepository {

    override suspend fun saveTransaction(transaction: Transaction) =
        db.transactionsDao().saveTransaction(transaction)

    override suspend fun searchTransactions(
        query: String,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = runCatching {
        db.transactionsDao().searchTransactions(query, startDate, endDate)
    }.mapCatching {
        transactionDtoToTransactionMapper.mapList(it)
    }.getOrThrow()

    override suspend fun getTransactions(
        filters: FiltersUiModel?,
        query: String
    ): List<Transaction> = withContext(ioDispatcher) {
        filters ?: return@withContext getAllTransactions()

        // TODO: Figure out different way for this to not create modular dependencies
        val (startDate, endDate) = filters.getStartAndEndDate()

        query.takeIf { it.isNotBlank() }?.run {
            return@withContext searchTransactions(query, startDate, endDate)
        }

        return@withContext filters.handleTransactions(startDate, endDate)
    }

    override suspend fun getAllTransactions(): List<Transaction> =
        runCatching {
            db.transactionsDao().getAll()
        }.mapCatching {
            transactionDtoToTransactionMapper.mapList(it)
        }.getOrThrow()

    // TODO: Create BalanceData data class
    override suspend fun calculateExpensesAndIncomes(): Pair<Double, Double> =
        withContext(ioDispatcher) {
            runCatching {
                val expensesDeferred = async { db.transactionsDao().getExpenses() }
                val incomesDeferred = async { db.transactionsDao().getIncomes() }
                expensesDeferred to incomesDeferred
            }.mapCatching { (expensesDeferred, incomesDeferred) ->
                Pair(
                    expensesDeferred.await().sumOf { -it.transactionAmount },
                    incomesDeferred.await().sumOf { it.transactionAmount }
                )
            }.getOrThrow()
        }

    private suspend fun FiltersUiModel.handleTransactions(
        startDate: Date,
        endDate: Date
    ): List<Transaction> = when (showTransactions) {
        ShowTransactions.SHOW_EXPENSES -> sortBy.getSortedExpenses(
            isDescending,
            startDate,
            endDate
        )
        ShowTransactions.SHOW_INCOMES -> sortBy.getSortedIncomes(
            isDescending,
            startDate,
            endDate
        )
        else -> sortBy.getSortedTransactions(isDescending, startDate, endDate)
    }

    private suspend fun SortBy.getSortedExpenses(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = when (this) {
        SortBy.SORT_BY_DATE ->
            getExpensesSortedByDate(isDescending, startDate, endDate)
        SortBy.SORT_BY_CATEGORY ->
            getExpensesSortedByCategory(isDescending, startDate, endDate)
        SortBy.SORT_BY_AMOUNT ->
            getExpensesSortedByAmount(isDescending, startDate, endDate)
    }

    private suspend fun SortBy.getSortedIncomes(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = when (this) {
        SortBy.SORT_BY_DATE ->
            getIncomesSortedByDate(isDescending, startDate, endDate)
        SortBy.SORT_BY_CATEGORY ->
            getIncomesSortedByCategory(isDescending, startDate, endDate)
        SortBy.SORT_BY_AMOUNT ->
            getIncomesSortedByAmount(isDescending, startDate, endDate)
    }

    private suspend fun SortBy.getSortedTransactions(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = when (this) {
        SortBy.SORT_BY_DATE ->
            getTransactionsSortedByDate(isDescending, startDate, endDate)
        SortBy.SORT_BY_CATEGORY ->
            getTransactionsSortedByCategory(isDescending, startDate, endDate)
        SortBy.SORT_BY_AMOUNT ->
            getTransactionsSortedByAmount(isDescending, startDate, endDate)
    }

    private suspend fun getExpensesSortedByDate(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = db.transactionsDao().runCatching {
        if (isDescending) getAllExpensesSortedByDateDescending(startDate, endDate)
        else getAllExpensesSortedByDateAscending(startDate, endDate)
    }.mapCatching {
        transactionDtoToTransactionMapper.mapList(it)
    }.getOrThrow()

    private suspend fun getExpensesSortedByCategory(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = db.transactionsDao().runCatching {
        if (isDescending) getAllExpensesSortedByCategoryDescending(startDate, endDate)
        else getAllExpensesSortedByCategoryAscending(startDate, endDate)
    }.mapCatching {
        transactionDtoToTransactionMapper.mapList(it)
    }.getOrThrow()

    private suspend fun getExpensesSortedByAmount(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = db.transactionsDao().runCatching {
        if (isDescending) getAllExpensesSortedByAmountDescending(startDate, endDate)
        else getAllExpensesSortedByAmountAscending(startDate, endDate)
    }.mapCatching {
        transactionDtoToTransactionMapper.mapList(it)
    }.getOrThrow()

    private suspend fun getIncomesSortedByDate(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = db.transactionsDao().runCatching {
        if (isDescending) getAllIncomesSortedByDateDescending(startDate, endDate)
        else getAllIncomesSortedByDateAscending(startDate, endDate)
    }.mapCatching {
        transactionDtoToTransactionMapper.mapList(it)
    }.getOrThrow()

    private suspend fun getIncomesSortedByCategory(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = db.transactionsDao().runCatching {
        if (isDescending) getAllIncomesSortedByCategoryDescending(startDate, endDate)
        else getAllIncomesSortedByCategoryAscending(startDate, endDate)
    }.mapCatching {
        transactionDtoToTransactionMapper.mapList(it)
    }.getOrThrow()

    private suspend fun getIncomesSortedByAmount(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = db.transactionsDao().runCatching {
        if (isDescending) getAllIncomesSortedByAmountDescending(startDate, endDate)
        else getAllIncomesSortedByAmountAscending(startDate, endDate)
    }.mapCatching {
        transactionDtoToTransactionMapper.mapList(it)
    }.getOrThrow()

    private suspend fun getTransactionsSortedByDate(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = db.transactionsDao().runCatching {
        if (isDescending) getAllTransactionsSortedByDateDescending(startDate, endDate)
        else getAllTransactionsSortedByDateAscending(startDate, endDate)
    }.mapCatching {
        transactionDtoToTransactionMapper.mapList(it)
    }.getOrThrow()

    private suspend fun getTransactionsSortedByCategory(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = db.transactionsDao().runCatching {
        if (isDescending) getAllTransactionsSortedByCategoryDescending(startDate, endDate)
        else getAllTransactionsSortedByCategoryAscending(startDate, endDate)
    }.mapCatching {
        transactionDtoToTransactionMapper.mapList(it)
    }.getOrThrow()

    private suspend fun getTransactionsSortedByAmount(
        isDescending: Boolean,
        startDate: Date,
        endDate: Date
    ): List<Transaction> = db.transactionsDao().runCatching {
        if (isDescending) getAllTransactionsSortedByAmountDescending(startDate, endDate)
        else getAllTransactionsSortedByAmountAscending(startDate, endDate)
    }.mapCatching {
        transactionDtoToTransactionMapper.mapList(it)
    }.getOrThrow()
}
