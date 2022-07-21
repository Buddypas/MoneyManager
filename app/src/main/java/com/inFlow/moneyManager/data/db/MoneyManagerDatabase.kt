package com.inFlow.moneyManager.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.inFlow.moneyManager.data.db.entities.CategoriesDao
import com.inFlow.moneyManager.data.db.entities.CategoryDto
import com.inFlow.moneyManager.data.db.entities.TransactionDto
import com.inFlow.moneyManager.data.db.entities.TransactionsDao
import com.inFlow.moneyManager.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [TransactionDto::class, CategoryDto::class], version = 2)
@TypeConverters(Converters::class)
abstract class MoneyManagerDatabase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionsDao
    abstract fun categoriesDao(): CategoriesDao

    class Callback @Inject constructor(
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
                        categoryName = "Car",
                        categoryType = "expense",
                    ),
                    CategoryDto(
                        categoryName = "Health",
                        categoryType = "expense"
                    ),
                    CategoryDto(
                        categoryName = "Salary",
                        categoryType = "income"
                    )
                )
            }
        }
    }

}