package com.inFlow.moneyManager.domain.di

import com.inFlow.moneyManager.domain.category.repository.CategoryRepository
import com.inFlow.moneyManager.domain.category.usecase.GetCategoriesUseCase
import com.inFlow.moneyManager.domain.category.usecase.GetExpenseCategoriesUseCase
import com.inFlow.moneyManager.domain.category.usecase.GetIncomeCategoriesUseCase
import com.inFlow.moneyManager.domain.category.usecase.SaveCategoryUseCase
import com.inFlow.moneyManager.domain.transaction.BalanceDataToBalanceDataUiModelMapper
import com.inFlow.moneyManager.domain.transaction.repository.TransactionRepository
import com.inFlow.moneyManager.domain.transaction.usecase.GetExpensesAndIncomesUseCase
import com.inFlow.moneyManager.domain.transaction.usecase.GetTransactionsUseCase
import com.inFlow.moneyManager.domain.transaction.usecase.SaveTransactionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideGetCategoriesUseCase(categoryRepository: CategoryRepository) =
        GetCategoriesUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideGetExpenseCategoriesUseCase(categoryRepository: CategoryRepository) =
        GetExpenseCategoriesUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideGetIncomeCategoriesUseCase(categoryRepository: CategoryRepository) =
        GetIncomeCategoriesUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideSaveCategoryUseCase(categoryRepository: CategoryRepository) =
        SaveCategoryUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideGetTransactionsUseCase(transactionRepository: TransactionRepository) =
        GetTransactionsUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideSaveTransactionUseCase(transactionRepository: TransactionRepository) =
        SaveTransactionUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetExpensesAndIncomesUseCase(
        transactionRepository: TransactionRepository,
        balanceDataToBalanceDataUiModelMapper: BalanceDataToBalanceDataUiModelMapper
    ): GetExpensesAndIncomesUseCase =
        GetExpensesAndIncomesUseCase(transactionRepository, balanceDataToBalanceDataUiModelMapper)
}
