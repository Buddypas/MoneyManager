package com.inFlow.moneyManager.db.entities

import androidx.room.*
import java.time.LocalDate
import java.time.LocalDateTime

// TODO: Research FTS4 - https://developer.android.com/training/data-storage/room/defining-data#fts
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val transactionId: Int,
    val amount: Double,
    val date: LocalDateTime,
    // category id
    val transactionCategoryId: Int,
    // balance after this transaction
    val balance: Double
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE transactionId=:id")
    fun getById(id: Int): Transaction

    /**
     * Month is zero based
     */
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    fun getTransactionsInRange(startDate: LocalDate, endDate: LocalDate): List<Transaction>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate AND amount < 0")
    fun getExpensesInRange(startDate: LocalDate, endDate: LocalDate): List<Transaction>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate AND amount > 0")
    fun getIncomesInRange(startDate: LocalDate, endDate: LocalDate): List<Transaction>

    @Insert
    fun insertAll(vararg transactions: Transaction)

    @Delete
    fun delete(transaction: Transaction)
}