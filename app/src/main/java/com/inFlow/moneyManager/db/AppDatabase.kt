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
import com.inFlow.moneyManager.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Transaction::class, Category::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionsDao
    abstract fun categoriesDao(): CategoriesDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val categoriesDao = database.get().categoriesDao()
            val transactionsDao = database.get().transactionsDao()

            applicationScope.launch {
                categoriesDao.insertAll(
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
        }
    }

}