package com.inFlow.moneyManager.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.inFlow.moneyManager.data.db.dao.CategoriesDao
import com.inFlow.moneyManager.data.db.entity.CategoryDto
import com.inFlow.moneyManager.data.db.entity.TransactionDto
import com.inFlow.moneyManager.data.db.dao.TransactionsDao
import com.inFlow.moneyManager.data.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [TransactionDto::class, CategoryDto::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MoneyManagerDatabase : RoomDatabase() {

    abstract fun transactionsDao(): TransactionsDao
    abstract fun categoriesDao(): CategoriesDao

    class RoomCallback @Inject constructor(
        private val database: Provider<MoneyManagerDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val categoriesDao = database.get().categoriesDao()
            val transactionsDao = database.get().transactionsDao()

            applicationScope.launch {
                categoriesDao.insertAll(
                    CategoryDto(
                        categoryId = 1,
                        categoryName = "Car",
                        categoryType = "expense"
                    ),
                    CategoryDto(
                        categoryId = 2,
                        categoryName = "Health",
                        categoryType = "expense"
                    ),
                    CategoryDto(
                        categoryId = 3,
                        categoryName = "Salary",
                        categoryType = "income"
                    )
                )

                transactionsDao.insertAll(
                    TransactionDto(
                        transactionAmount = 650.0,
                        transactionDate = Date(),
                        transactionCategoryId = 3,
                        transactionBalanceAfter = 650.0,
                        transactionDescription = "T1"
                    ),
                    TransactionDto(
                        transactionAmount = -50.0,
                        transactionDate = Date(),
                        transactionCategoryId = 2,
                        transactionBalanceAfter = 600.0,
                        transactionDescription = "T2"
                    ),
                    TransactionDto(
                        transactionAmount = -100.0,
                        transactionDate = Date(),
                        transactionCategoryId = 1,
                        transactionBalanceAfter = 500.0,
                        transactionDescription = "T3"
                    )
                )
            }
        }
    }
}
