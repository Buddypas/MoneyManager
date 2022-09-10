package com.inFlow.moneyManager.domain.di

import com.inFlow.moneyManager.domain.transaction.BalanceDataToBalanceDataUiModelMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {
    @Provides
    @Singleton
    fun provideBalanceDataToBalanceDataUiModelMapper() =
        BalanceDataToBalanceDataUiModelMapper()
}