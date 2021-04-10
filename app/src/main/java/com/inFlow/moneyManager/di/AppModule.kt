package com.inFlow.moneyManager.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.inFlow.moneyManager.db.AppDatabase
import com.inFlow.moneyManager.db.entities.CategoriesDao
import com.inFlow.moneyManager.db.entities.TransactionsDao
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.concurrent.Executors

val dataModule = module {

    fun provideDatabase(application: Application): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideTransactionsDao(database: AppDatabase): TransactionsDao {
        return database.transactionsDao()
    }

    fun provideCategoriesDao(database: AppDatabase): CategoriesDao {
        return database.categoriesDao()
    }

    single { provideDatabase(androidApplication()) }
    single { provideCategoriesDao(get()) }
    single { provideTransactionsDao(get()) }

    single { AppRepository(get()) }
}

@ExperimentalCoroutinesApi
val viewModelModule = module {
    factory { DashboardViewModel(get()) }
}

//val miscModule = module {
//}