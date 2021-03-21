package com.inFlow.moneyManager.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.inFlow.moneyManager.db.entities.CategoriesDao
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction
import com.inFlow.moneyManager.db.entities.TransactionsDao
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Transaction::class, Category::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionsDao
    abstract fun categoriesDao(): CategoriesDao

}