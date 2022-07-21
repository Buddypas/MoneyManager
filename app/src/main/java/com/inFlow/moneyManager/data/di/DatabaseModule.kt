package com.inFlow.moneyManager.data.di

import android.content.Context
import androidx.room.Room
import com.inFlow.moneyManager.data.db.MoneyManagerDatabase
import com.inFlow.moneyManager.data.db.entities.CategoriesDao
import com.inFlow.moneyManager.data.db.entities.TransactionsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    // TODO: Try with separate module

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MoneyManagerDatabase {
        return Room
            .databaseBuilder(appContext, MoneyManagerDatabase::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTransactionsDao(database: MoneyManagerDatabase): TransactionsDao {
        return database.transactionsDao()
    }

    @Provides
    fun provideCategoriesDao(database: MoneyManagerDatabase): CategoriesDao {
        return database.categoriesDao()
    }
}
