package com.inFlow.moneyManager.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction
import com.inFlow.moneyManager.db.entities.TransactionsDao

@Database(entities = [Transaction::class, Category::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase(): RoomDatabase() {
    abstract fun transactionsDao() : TransactionsDao
}