package com.inFlow.moneyManager.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

// TODO: Research FTS4 - https://developer.android.com/training/data-storage/room/defining-data#fts
@Entity(tableName = "transactions")
data class TransactionDto(
    @PrimaryKey(autoGenerate = true) val transactionId: Int = 0,
    val transactionAmount: Double,
    val transactionDate: Date,
    val transactionDescription: String,
    // category id
    val transactionCategoryId: Int,
    // balance after this transaction
    val transactionBalanceAfter: Double
)

