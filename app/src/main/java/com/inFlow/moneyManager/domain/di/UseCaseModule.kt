package com.inFlow.moneyManager.domain.di

import com.inFlow.moneyManager.domain.category.repository.CategoryRepository
import com.inFlow.moneyManager.domain.category.usecase.*
import com.inFlow.moneyManager.domain.transaction.mapper.BalanceDataToBalanceDataUiModelMapper
import com.inFlow.moneyManager.domain.transaction.repository.TransactionRepository
import com.inFlow.moneyManager.domain.transaction.usecase.GetExpensesAndIncomesUseCase
import com.inFlow.moneyManager.domain.transaction.usecase.GetTransactionsUseCase
import com.inFlow.moneyManager.domain.transaction.usecase.SaveTransactionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetCategoriesUseCase(categoryRepository: CategoryRepository) =
        GetCategoriesUseCase(categoryRepository)

    @Provides
    fun provideGetExpenseCategoriesUseCase(categoryRepository: CategoryRepository) =
        GetExpenseCategoriesUseCase(categoryRepository)

    @Provides
    fun provideGetIncomeCategoriesUseCase(categoryRepository: CategoryRepository) =
        GetIncomeCategoriesUseCase(categoryRepository)

    @Provides
    fun provideSaveCategoryUseCase(categoryRepository: CategoryRepository): SaveCategoryUseCase =
        SaveCategoryUseCase(categoryRepository)

    @Provides
    fun provideUpdateCategoryUseCase(categoryRepository: CategoryRepository): UpdateCategoryUseCase =
        UpdateCategoryUseCase(categoryRepository)

    @Provides
    fun provideGetTransactionsUseCase(transactionRepository: TransactionRepository) =
        GetTransactionsUseCase(transactionRepository)

    @Provides
    fun provideSaveTransactionUseCase(transactionRepository: TransactionRepository) =
        SaveTransactionUseCase(transactionRepository)

    @Provides
    fun provideGetExpensesAndIncomesUseCase(
        transactionRepository: TransactionRepository,
        balanceDataToBalanceDataUiModelMapper: BalanceDataToBalanceDataUiModelMapper
    ): GetExpensesAndIncomesUseCase =
        GetExpensesAndIncomesUseCase(transactionRepository, balanceDataToBalanceDataUiModelMapper)
}
