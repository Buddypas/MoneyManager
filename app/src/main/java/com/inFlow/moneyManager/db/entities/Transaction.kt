package com.inFlow.moneyManager.db.entities

import androidx.room.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

// TODO: Research FTS4 - https://developer.android.com/training/data-storage/room/defining-data#fts
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val transactionId: Int,
    val transactionAmount: Double,
    val transactionDate: Date,
    // category id
    val transactionCategoryId: Int,
    // balance after this transaction
    val transactionBalanceAfter: Double
)

@Dao
interface TransactionsDao {
    @Query("SELECT * FROM transactions")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE transactionId=:id")
    fun getById(id: Int): Transaction

    @Update
    fun updateTransactions(vararg transactions: Transaction)

    /**
     * Month is zero based
     */
    @Query("SELECT * FROM transactions WHERE transactionDate BETWEEN :startDate AND :endDate")
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
    fun getMostRecentTransaction(): Transaction

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg transactions: Transaction)

    @Delete
    fun delete(transaction: Transaction)
}