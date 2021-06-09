package com.inFlow.moneyManager.di

import android.content.Context
import androidx.room.Room
import com.inFlow.moneyManager.db.AppDatabase
import com.inFlow.moneyManager.db.entities.CategoriesDao
import com.inFlow.moneyManager.db.entities.TransactionsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room
            .databaseBuilder(appContext, AppDatabase::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTransactionsDao(database: AppDatabase): TransactionsDao {
        return database.transactionsDao()
    }

    @Provides
    fun provideCategoriesDao(database: AppDatabase): CategoriesDao {
        return database.categoriesDao()
    }

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

//val dataModule = module {
//
//    fun provideDatabase(application: Application): AppDatabase {
//        return Room
//            .databaseBuilder(application, AppDatabase::class.java, "app.db")
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    fun provideTransactionsDao(database: AppDatabase): TransactionsDao {
//        return database.transactionsDao()
//    }
//
//    fun provideCategoriesDao(database: AppDatabase): CategoriesDao {
//        return database.categoriesDao()
//    }
//
//    single { provideDatabase(androidApplication()) }
//    single { provideCategoriesDao(get()) }
//    single { provideTransactionsDao(get()) }
//
//    single { AppRepository(get()) }
//}
//
//@ExperimentalCoroutinesApi
//val viewModelModule = module {
//    viewModel { DashboardViewModel(get()) }
//    viewModel { CategoriesViewModel(get()) }
//    viewModel { FiltersViewModel() }
//    viewModel { AddTransactionViewModel(get()) }
//    viewModel { AddCategoryViewModel(get()) }
//}