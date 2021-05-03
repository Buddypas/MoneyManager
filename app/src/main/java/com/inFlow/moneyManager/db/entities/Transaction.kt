package com.inFlow.moneyManager.db.entities

import androidx.room.*
import com.inFlow.moneyManager.db.relations.CategoriesWithTransactions
import com.inFlow.moneyManager.ui.dashboard.PeriodMode
import com.inFlow.moneyManager.vo.FiltersDto
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.*

// TODO: Research FTS4 - https://developer.android.com/training/data-storage/room/defining-data#fts
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val transactionId: Int = 0,
    val transactionAmount: Double,
    val transactionDate: Date,
    val transactionDescription: String,
    // category id
    val transactionCategoryId: Int,
    // balance after this transaction
    val transactionBalanceAfter: Double
)

@Dao
interface TransactionsDao {

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions " +
                "WHERE transactionAmount > 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionDate DESC"
    )
    fun getAllIncomesSortedByDateDescending(startDate: Date, endDate: Date): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionAmount > 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionDate ASC"
    )
    fun getAllIncomesSortedByDateAscending(startDate: Date, endDate: Date): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionAmount > 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionCategoryId DESC"
    )
    fun getAllIncomesSortedByCategoryDescending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionAmount > 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionCategoryId ASC"
    )
    fun getAllIncomesSortedByCategoryAscending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionAmount > 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionAmount DESC"
    )
    fun getAllIncomesSortedByAmountDescending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionAmount > 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionAmount ASC"
    )
    fun getAllIncomesSortedByAmountAscending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions " +
                "WHERE transactionAmount < 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionDate DESC"
    )
    fun getAllExpensesSortedByDateDescending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionAmount < 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionDate ASC"
    )
    fun getAllExpensesSortedByDateAscending(startDate: Date, endDate: Date): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionAmount < 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionCategoryId DESC"
    )
    fun getAllExpensesSortedByCategoryDescending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionAmount < 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionCategoryId ASC"
    )
    fun getAllExpensesSortedByCategoryAscending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionAmount < 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionAmount DESC"
    )
    fun getAllExpensesSortedByAmountDescending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionAmount < 0 AND transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionAmount ASC"
    )
    fun getAllExpensesSortedByAmountAscending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions " +
                "WHERE transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionDate DESC"
    )
    fun getAllTransactionsSortedByDateDescending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionDate ASC"
    )
    fun getAllTransactionsSortedByDateAscending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionCategoryId DESC"
    )
    fun getAllTransactionsSortedByCategoryDescending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionCategoryId ASC"
    )
    fun getAllTransactionsSortedByCategoryAscending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionAmount DESC"
    )
    fun getAllTransactionsSortedByAmountDescending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY transactionAmount ASC"
    )
    fun getAllTransactionsSortedByAmountAscending(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE transactionDescription LIKE '%' || :query || '%' AND transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY transactionDate DESC")
    fun searchTransactions(query: String, startDate: Date,
                           endDate: Date): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE transactionId=:id")
    fun getById(id: Int): Transaction

    @Update
    fun updateTransactions(vararg transactions: Transaction)

    /**
     * Month is zero based
     */
    @Query("SELECT * FROM transactions WHERE transactionDate BETWEEN :startDate AND :endDate ")
    fun getTransactionsInRange(startDate: Date, endDate: Date): List<Transaction>

    @Query("SELECT * FROM transactions WHERE transactionDate BETWEEN :startDate AND :endDate AND transactionAmount < 0")
    fun getExpensesInRange(startDate: Date, endDate: Date): List<Transaction>

    @Query("SELECT * FROM transactions WHERE transactionDate BETWEEN :startDate AND :endDate AND transactionAmount > 0")
    fun getIncomesInRange(startDate: Date, endDate: Date): List<Transaction>

    @Query("SELECT * FROM transactions WHERE transactionCategoryId=:catId")
    fun getByCategoryId(catId: Int): List<Transaction>

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getAllDescending(): List<Transaction>

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC LIMIT 1")
    suspend fun getMostRecentTransaction(): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg transactions: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE transactionAmount < 0")
    suspend fun getExpenses(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE transactionAmount > 0")
    suspend fun getIncomes(): List<Transaction>
}