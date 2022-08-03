package com.inFlow.moneyManager.data.di

import android.content.Context
import androidx.room.Room
import com.inFlow.moneyManager.data.db.MoneyManagerDatabase
import com.inFlow.moneyManager.data.db.dao.CategoriesDao
import com.inFlow.moneyManager.data.db.dao.TransactionsDao
import com.inFlow.moneyManager.data.mapper.CategoryDtoToCategoryMapper
import com.inFlow.moneyManager.data.repository.CategoryRepositoryImpl
import com.inFlow.moneyManager.domain.category.repository.CategoryRepository
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
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context,
        callback: MoneyManagerDatabase.RoomCallback
    ): MoneyManagerDatabase =
        Room
            .databaseBuilder(appContext, MoneyManagerDatabase::class.java, "app.db")
            .addCallback(callback)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTransactionsDao(database: MoneyManagerDatabase): TransactionsDao =
        database.transactionsDao()

    @Provides
    fun provideCategoriesDao(database: MoneyManagerDatabase): CategoriesDao =
        database.categoriesDao()

    @Singleton
    @Provides
    fun provideCategoryRepository(
        database: MoneyManagerDatabase,
        categoryDtoToCategoryMapper: CategoryDtoToCategoryMapper
    ): CategoryRepository = CategoryRepositoryImpl(database, categoryDtoToCategoryMapper)

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
